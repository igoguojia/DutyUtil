package com.jnu.tool.task.timed.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jnu.tool.task.timed.pojo.TimedTaskInfo;
import com.jnu.tool.task.timed.pojo.TimedTaskLog;
import com.jnu.tool.task.timed.pojo.TimedTaskParam;
import com.jnu.tool.task.timed.service.api.TimedTaskInfoService;
import com.jnu.tool.task.timed.service.api.TimedTaskLogService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * @author duya001
 */
@Component
public class QuartzUtil {

    @Autowired
    private Scheduler scheduler;

    private final Logger LOGGER = Logger.getLogger("com.jnu.tool.task.timed.util.QuartzUtil");
    private final Map<String, JobKey> jobKeyMap = new ConcurrentHashMap<>(16);

    private final TimedTaskLogService timedTaskLogService;

    private final TimedTaskInfoService timedTaskInfoService;

    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public QuartzUtil(TimedTaskLogService timedTaskLogService, TimedTaskInfoService timedTaskInfoService) {
        this.timedTaskLogService = timedTaskLogService;
        this.timedTaskInfoService = timedTaskInfoService;
    }

    public void createTask(TimedTaskParam taskParam) throws JsonProcessingException, ParseException {
        String userID = taskParam.getUserID();
        String task = taskParam.getTask();
        String taskType = taskParam.getTaskType();
        String startTime = taskParam.getStartTime();
        String endTime = taskParam.getEndTime();
        String cron = taskParam.getCron();
        String taskAlias = taskParam.getTaskAlias();
        String flag = "1";

        // 获取当前时间，用于记录日志
        Date currentTimeDate = new Date();
        // 日期格式
        String currentTime = format.format(currentTimeDate);
        // 设置日志
        TimedTaskLog log = new TimedTaskLog(userID, taskAlias, task, taskType,
                startTime, endTime, cron, "null", "null", currentTime, flag);

        TimedTaskInfo info = timedTaskInfoService.searchTaskByAlias(taskAlias);

        if ("0".equals(taskType)) {
            // cmd任务
            executeCmd(log, task, taskAlias, format.parse(startTime), format.parse(endTime), cron, info);
        } else if ("1".equals(taskType)) {
            // 方法任务
            List<Map<String, Object>> params = taskParam.getParams();
            params = (params == null || params.size() == 0 ? new ArrayList<>(0) : params);
            log.setMethodParam(new ObjectMapper().writeValueAsString(params));
            info.setParams(new ObjectMapper().writeValueAsString(params));
            executeMethod(log, params, cron, task, taskAlias, format.parse(startTime), format.parse(endTime), info);
        } else {
            LOGGER.warning("任务类型错误！");
            log.setMsgInfo("任务类型错误！");
            timedTaskLogService.addTimedTaskLog(log);
        }
    }

    public void pauseTask(String taskAlias) throws SchedulerException {

        TimedTaskInfo info = timedTaskInfoService.searchTaskByAlias(taskAlias);
        // 获取当前时间，用于记录日志
        Date currentTimeDate = new Date();
        // 日期格式
        String currentTime = format.format(currentTimeDate);

        TimedTaskLog log = new TimedTaskLog(info.getUserID(), taskAlias, info.getTaskName(), info.getTaskType(),
                info.getStartTime(), info.getEndTime(), info.getCron(), info.getParams(), "null", currentTime, info.getFlag());

        JobKey jobKey = jobKeyMap.get(taskAlias);
        if (jobKey != null) {
            scheduler.pauseJob(jobKey);
        }
        LOGGER.info("任务" + taskAlias + "停止成功！(任务列表)");
        log.setMsgInfo("停止任务成功（任务列表）");
        log.setFlag("0");
        timedTaskLogService.addTimedTaskLog(log);
    }

    public void startTask(String taskAlias) throws SchedulerException {
        TimedTaskInfo info = timedTaskInfoService.searchTaskByAlias(taskAlias);

        // 获取当前时间，用于记录日志
        Date currentTimeDate = new Date();
        // 日期格式
        String currentTime = format.format(currentTimeDate);

        TimedTaskLog log = new TimedTaskLog(info.getUserID(), taskAlias, info.getTaskName(), info.getTaskType(),
                info.getStartTime(), info.getEndTime(), info.getCron(), info.getParams(), "null", currentTime, info.getFlag());

        JobKey jobKey = jobKeyMap.get(taskAlias);
        if (jobKey != null) {
            scheduler.resumeJob(jobKey);
        }
        LOGGER.info("任务" + taskAlias + "启动成功成功！(任务列表)");
        log.setMsgInfo("启动任务成功");
        log.setFlag("1");
        timedTaskLogService.addTimedTaskLog(log);
    }

    public void deleteTask(String taskAlias) throws SchedulerException {
        TimedTaskInfo info = timedTaskInfoService.searchTaskByAlias(taskAlias);

        // 获取当前时间，用于记录日志
        Date currentTimeDate = new Date();
        // 日期格式
        String currentTime = format.format(currentTimeDate);

        JobKey jobKey = jobKeyMap.get(taskAlias);
        if (jobKey != null) {
            scheduler.deleteJob(jobKey);
            jobKeyMap.remove(taskAlias);
            LOGGER.info("任务" + taskAlias + "删除成功！(任务列表)");
        }
        if (info != null) {
            TimedTaskLog log = new TimedTaskLog(info.getUserID(), taskAlias, info.getTaskName(), info.getTaskType(),
                    info.getStartTime(), info.getEndTime(), info.getCron(), info.getParams(), "null", currentTime, info.getFlag());
            log.setMsgInfo("删除任务成功");
            log.setFlag("0");
            timedTaskLogService.addTimedTaskLog(log);
        }

    }

    private void executeMethod(TimedTaskLog log, List<Map<String, Object>> params, String cron, String task, String taskAlias, Date startTime, Date endTime, TimedTaskInfo info) throws JsonProcessingException {
        // 工具类对象
        TimedTaskUtil util = new TimedTaskUtil();
        // 获取类名和方法名
        String[] names = util.getClassAndMethodName(task);
        if (names == null) {
            log.setMsgInfo("非法任务!");
            timedTaskLogService.addTimedTaskLog(log);
            return;
        }
        String className = names[0];
        String methodName = names[1];
        // 判断类名和方法名是否合法
        String legalResult = util.isLegalMethod(className, methodName);
        if (!"".equals(legalResult)) {
            log.setMsgInfo("非法任务");
            timedTaskLogService.addTimedTaskLog(log);
            return;
        }
        Class<?> aClass = util.getaClass();
        Class<?>[] paramsType = util.handleParams(params);
        if (paramsType == null) {
            log.setMsgInfo("参数不合法");
            timedTaskLogService.addTimedTaskLog(log);
            return;
        }
        // 封装执行反射所需要的参数
        Map<String, Object> map = new HashMap<>(16);
        map.put("params", params);
        map.put("className", className);
        map.put("methodName", methodName);
        map.put("aClass", aClass);
        map.put("paramsType", paramsType);
        map.put("log", log);
        JobDataMap dataMap = new JobDataMap(map);
        // 分配定时任务
        String identity = UUID.randomUUID().toString().substring(0, 5);
        String group = UUID.randomUUID().toString().substring(0, 5);
        // 指定定时任务类为MyJob
        JobDetail detail = JobBuilder.newJob(MyJob.class).usingJobData(identity, group)
                .usingJobData(dataMap).build();
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        scheduleBuilder.withMisfireHandlingInstructionDoNothing();
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(identity, group).startAt(startTime).endAt(endTime)
                .withSchedule(scheduleBuilder).build();
        try {
            execute(detail, cronTrigger, taskAlias, info, log);
        } catch (SchedulerException e) {
            LOGGER.info("写入任务列表失败!" + e);
            log.setMsgInfo("写入任务列表失败!SchedulerException错误");
            timedTaskLogService.addTimedTaskLog(log);
        }
    }

    private void executeCmd(TimedTaskLog log, String task, String taskAlias,
                            Date startTime, Date endTime, String cron, TimedTaskInfo info) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("task", task);
        map.put("log", log);
        JobDataMap dataMap = new JobDataMap(map);
        // 分配定时任务
        String identity = UUID.randomUUID().toString().substring(0, 5);
        String group = UUID.randomUUID().toString().substring(0, 5);
        // 指定定时任务类为MyJob
        JobDetail detail = JobBuilder.newJob(MyCmd.class).usingJobData(identity, group)
                .usingJobData(dataMap).build();
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        scheduleBuilder.withMisfireHandlingInstructionDoNothing();
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(identity, group).startAt(startTime).endAt(endTime)
                .withSchedule(scheduleBuilder).build();


//        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(identity, group).startAt(startTime).endAt(endTime)
//                .withSchedule(CronScheduleBuilder.cronSchedule(cron)).forJob(detail.getKey()).build();
        try {
            execute(detail, cronTrigger, taskAlias, info, log);
        } catch (SchedulerException e) {
            LOGGER.info("写入任务列表失败!" + e);
            log.setMsgInfo("写入任务列表失败!SchedulerException错误");
            timedTaskLogService.addTimedTaskLog(log);
        }
    }

    private void execute(JobDetail detail, CronTrigger cronTrigger, String taskAlias, TimedTaskInfo info, TimedTaskLog log) throws SchedulerException {
        scheduler.scheduleJob(detail, cronTrigger);
        // 启动任务
        scheduler.start();
        JobKey key = detail.getKey();
        jobKeyMap.put(taskAlias, key);
        boolean success = timedTaskInfoService.addTask(info);
        if (success) {
            LOGGER.info("成功将" + info.getTaskAlias() + "写入任务列表！");
        } else {
            LOGGER.warning("记录添加失败！");
            log.setMsgInfo("记录添加失败!");
            timedTaskLogService.addTimedTaskLog(log);
            return;
        }
        Date currentTime = new Date();
        // 日期格式
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTimeString = format.format(currentTime);
        log.setMsgInfo("写入任务列表成功");
        log.setSubmitTime(currentTimeString);
        timedTaskLogService.addTimedTaskLog(log);
    }
}
