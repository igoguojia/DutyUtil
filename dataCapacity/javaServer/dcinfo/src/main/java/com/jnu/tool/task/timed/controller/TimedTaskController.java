package com.jnu.tool.task.timed.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iceolive.util.StringUtil;
import com.jnu.tool.authority.service.api.UserAuthorityService;
import com.jnu.tool.task.timed.mapper.TimedTaskLogMapper;
import com.jnu.tool.task.timed.pojo.TimedTaskInfo;
import com.jnu.tool.task.timed.pojo.TimedTaskLog;
import com.jnu.tool.task.timed.pojo.TimedTaskParam;
import com.jnu.tool.task.timed.service.api.TimedTaskInfoService;
import com.jnu.tool.task.timed.service.api.TimedTaskLogService;
import com.jnu.tool.utils.ResultEntity;
import com.jnu.tool.variable.service.api.VariableService;
import org.javatuples.Pair;
import org.quartz.CronExpression;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * "0/1 * * * * ?"
 *
 * @author duya001
 */
@CrossOrigin
@RestController
@RequestMapping("/timedTask")
public class TimedTaskController {
    private final TimedTaskLogService timedTaskLogService;
    private final VariableService variableService;
    private final Map<String, JobKey> jobKeyMap = new ConcurrentHashMap<>(16);
    private final TimedTaskInfoService timedTaskInfoService;
    private final UserAuthorityService userAuthorityService;


    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @Qualifier("timedTaskLogMapper")
    @Autowired
    TimedTaskLogMapper timedTaskLogMapper;

    @Autowired
    private Scheduler scheduler;

    private final Logger LOGGER = Logger.getLogger("com.jnu.tool.task.timed.controller.TimedTask");

    public TimedTaskController(@Qualifier("timedTaskLogService") TimedTaskLogService timedTaskLogService,
                               @Qualifier("variableService") VariableService variableService,
                               @Qualifier("timedTaskInfoService") TimedTaskInfoService timedTaskInfoService,
                               @Qualifier("userAuthorityService") UserAuthorityService userAuthorityService) throws SchedulerException {
        this.timedTaskLogService = timedTaskLogService;
        this.variableService = variableService;
        this.timedTaskInfoService = timedTaskInfoService;
        this.userAuthorityService = userAuthorityService;
    }

    // @Access(level = AccessLevel.CreateTask)
    @PostMapping("/createTask")
    public String createTask(@RequestBody TimedTaskParam taskParam) throws JsonProcessingException {
        String userID = taskParam.getUserID();
        String task = taskParam.getTask();
        String taskType = taskParam.getTaskType();
        String startTimeString = taskParam.getStartTime();
        String endTimeString = taskParam.getEndTime();
        String cron = taskParam.getCron();
        String taskAlias = taskParam.getTaskAlias();
        String flag = "1"; //当前任务新增时，即启动

        // 如果参数存在，则对参数进行修剪，除去前后的空格
        userID = (userID == null ? null : userID.trim());
        task = (task == null ? null : task.trim());
        taskType = (taskType == null ? null : taskType.trim());
        cron = (cron == null ? null : cron.trim());
        taskAlias = (taskAlias == null ? null : taskAlias.trim());

        // 检测userID
        if (!StringUtil.isBlank(userID)) {
            try {
                userID = Integer.parseInt(userID) + "";
            } catch (NumberFormatException e) {
                return ResultEntity.failWithoutData("userID格式有误！").returnResult();
            }
        } else {
            LOGGER.warning("userID为空");
            return ResultEntity.failWithoutData("userID为空").returnResult();
        }

        if (timedTaskInfoService.searchTaskByAlias(taskAlias) != null) {
            LOGGER.warning("数据库中已有该别名" + taskAlias + "，请更换！");
            return ResultEntity.failWithoutData("数据库中已有该别名" + taskAlias + "，请更换！").returnResult();
        }
        // 检测taskID
        if (StringUtil.isBlank(taskAlias)) {
            // 如果taskID为空，则自动生成一个ID
            UUID uuid = UUID.randomUUID();
            taskAlias = uuid.toString().substring(0, 20);
        }
        // 检测开始和结束时间
        // 获取当前时间，用于记录日志
        Date currentTime = new Date();
        // 日期格式
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTimeString = format.format(currentTime);
        // 设置日志
        TimedTaskLog log = new TimedTaskLog(userID, taskAlias, task, taskType,
                startTimeString, endTimeString, cron, "null", "null", currentTimeString, flag);
        // 对开始时间和结束时间做检测

        try {
            format.parse(startTimeString);
        } catch (ParseException e) {
            LOGGER.warning("开始时间格式有误!");
            return returnFailMsg(log, "开始时间格式有误!");
        }
        try {
            format.parse(endTimeString);
        } catch (ParseException e) {
            LOGGER.warning("结束时间格式有误!");
            return returnFailMsg(log, "结束时间格式有误!");
        }
        // 检测其他参数
        if (StringUtil.isBlank(cron) || StringUtil.isBlank(task)) {
            return returnFailMsg(log, "非法入参！");
        }
        // 创建注册表记录
        TimedTaskInfo info = new TimedTaskInfo(userID, taskAlias, task, taskType,
                startTimeString, endTimeString, cron, "null", flag);
        // 判断cron表达式合法性
        if (!CronExpression.isValidExpression(cron)) {
            return returnFailMsg(log, "Cron表达式不合法!");
        }

        if ("1".equals(taskType)) {
            // 方法任务
            List<Map<String, Object>> params = taskParam.getParams();
            params = (params == null || params.size() == 0 ? new ArrayList<>(0) : params);
            log.setMethodParam(new ObjectMapper().writeValueAsString(params));
            info.setParams(new ObjectMapper().writeValueAsString(params));
        } else if (!"0".equals(taskType)) {
            LOGGER.warning("任务类型错误！");
            return ResultEntity.failWithoutData("任务类型错误！").returnResult();
        }

        boolean success = timedTaskInfoService.addTask(info);
        if (!success) {
            LOGGER.warning("记录添加失败！");
            return returnFailMsg(log, "添加任务失败!");
        } else {
            LOGGER.info("记录添加成功！");
        }
        log.setMsgInfo("创建成功");
        boolean b = timedTaskLogService.addTimedTaskLog(log);
        if (!b) {
            LOGGER.warning("写入日志表失败！");
            return ResultEntity.failWithoutData("写入日志表失败！").returnResult();
        }
        return ResultEntity.successWithData(taskAlias).returnResult();
    }


    // @Access(level = AccessLevel.startTask)
    @PostMapping("/startTask")
    public String startTask(@RequestParam(value = "userID", defaultValue = "") String userID,
                            @RequestParam(value = "taskAlias", defaultValue = "") String taskAlias) throws JsonProcessingException {

        if ("".equals(taskAlias)) {
            return ResultEntity.failWithoutData("任务别名为空，请重新输入！").returnResult();
        }
        // 检测userID
        if (!StringUtil.isBlank(userID)) {
            try {
                userID = Integer.parseInt(userID) + "";
            } catch (NumberFormatException e) {
                return ResultEntity.failWithoutData("userID格式有误！").returnResult();
            }
        } else {
            LOGGER.warning("userID为空");
            return ResultEntity.failWithoutData("userID为空").returnResult();
        }
        // 检测数据库是否包含该任务
        if (timedTaskInfoService.searchTaskByAlias(taskAlias) == null) {
            return ResultEntity.failWithoutData("当前数据库内无该别名，请重新输入！").returnResult();
        }
        TimedTaskInfo taskInfo = timedTaskInfoService.searchTaskByAlias(taskAlias);
        boolean success;
        // 权限判断
//        UserAuthority userAuthority = new UserAuthority(userID, "1000300101");
//        if (userID.equals(taskInfo.getUserID()) || userAuthorityService.searchUserAuthorityByUserId(userID).contains(userAuthority)) {
//            success = timedTaskInfoService.updateState(taskAlias, "1");
//        } else {
//            LOGGER.warning("用户无该权限");
//            return ResultEntity.failWithoutData("用户无该权限").returnResult();
//        }
        success = timedTaskInfoService.updateState(taskAlias, "1");

        // 获取当前时间，用于记录日志
        Date currentTime = new Date();
        // 日期格式
        String currentTimeString = format.format(currentTime);
        TimedTaskLog log = new TimedTaskLog(userID, taskAlias, taskInfo.getTaskName(), taskInfo.getTaskType(),
                taskInfo.getStartTime(), taskInfo.getEndTime(), taskInfo.getCron(), taskInfo.getParams(), "null", currentTimeString, "0");
        if (success) {
            LOGGER.info("启动任务成功！");
            log.setMsgInfo("启动任务成功");
            log.setFlag("1");
            boolean b = timedTaskLogService.addTimedTaskLog(log);
            if (!b) {
                LOGGER.warning("写入日志表失败！");
                return ResultEntity.failWithoutData("启动任务成功，写入日志表失败！").returnResult();
            }
        } else {
            LOGGER.warning("启动任务失败！");
            log.setMsgInfo("启动任务失败");
            boolean b = timedTaskLogService.addTimedTaskLog(log);
            if (!b) {
                LOGGER.warning("写入日志表失败！");
                return ResultEntity.failWithoutData("启动任务失败，写入日志表失败！").returnResult();
            }
            return ResultEntity.failWithoutData("启动任务失败！").returnResult();
        }
        return ResultEntity.successWithoutData().returnResult();

    }

    // @Access(level = AccessLevel.stopTask)
    @PostMapping("/stopTask")
    public String stopTask(@RequestParam(value = "userID", defaultValue = "") String userID,
                           @RequestParam(value = "taskAlias", defaultValue = "") String taskAlias) throws JsonProcessingException, SchedulerException {
        if ("".equals(taskAlias)) {
            return ResultEntity.failWithoutData("任务别名为空，请重新输入！").returnResult();
        }
        // 检测userID
        if (!StringUtil.isBlank(userID)) {
            try {
                userID = Integer.parseInt(userID) + "";
            } catch (NumberFormatException e) {
                return ResultEntity.failWithoutData("userID格式有误！").returnResult();
            }
        } else {
            LOGGER.warning("userID为空");
            return ResultEntity.failWithoutData("userID为空").returnResult();
        }
        if (timedTaskInfoService.searchTaskByAlias(taskAlias) == null) {
            return ResultEntity.failWithoutData("当前数据库内无该别名").returnResult();
        }
        boolean success;
        TimedTaskInfo taskInfo = timedTaskInfoService.searchTaskByAlias(taskAlias);
//        UserAuthority userAuthority = new UserAuthority(userID, "1000300101");
//        if (userID.equals(taskInfo.getUserID()) || userAuthorityService.searchUserAuthorityByUserId(userID).contains(userAuthority)) {
//            success = timedTaskInfoService.updateState(taskAlias, "0");
//        } else {
//            LOGGER.warning("用户无该权限");
//            return ResultEntity.failWithoutData("该用户无该权限").returnResult();
//        }
        success = timedTaskInfoService.updateState(taskAlias, "0");

        // 获取当前时间，用于记录日志
        Date currentTime = new Date();
        // 日期格式
        String currentTimeString = format.format(currentTime);
        TimedTaskLog log = new TimedTaskLog(userID, taskAlias, taskInfo.getTaskName(), taskInfo.getTaskType(),
                taskInfo.getStartTime(), taskInfo.getEndTime(), taskInfo.getCron(), taskInfo.getParams(), "null", currentTimeString, "1");

        if (success) {
            LOGGER.info("停止任务" + taskAlias + "成功！（数据库）");
            log.setMsgInfo("停止任务成功（数据库）");
            timedTaskLogService.addTimedTaskLog(log);
        } else {
            LOGGER.warning("停止任务" + taskAlias + "失败！");
            log.setMsgInfo("停止任务失败");
            timedTaskLogService.addTimedTaskLog(log);
            return ResultEntity.failWithoutData("停止任务" + taskAlias + "失败！").returnResult();
        }
        return ResultEntity.successWithoutData().returnResult();

    }

    // @Access(level = AccessLevel.deleteTask)
    @PostMapping("/deleteTask")
    public String deleteTask(@RequestParam(value = "userID", defaultValue = "") String userID,
                             @RequestParam(value = "taskAlias", defaultValue = "") String taskAlias) throws JsonProcessingException, SchedulerException {
        if ("".equals(taskAlias)) {
            return ResultEntity.failWithoutData("任务别名为空，请重新输入！").returnResult();
        }
        // 检测userID
        if (!StringUtil.isBlank(userID)) {
            try {
                userID = Integer.parseInt(userID) + "";
            } catch (NumberFormatException e) {
                return ResultEntity.failWithoutData("userID格式有误！").returnResult();
            }
        } else {
            LOGGER.warning("userID为空");
            return ResultEntity.failWithoutData("userID为空").returnResult();
        }
        if (timedTaskInfoService.searchTaskByAlias(taskAlias) == null) {
            LOGGER.warning("当前数据库内无该别名，请重新输入！");
            return ResultEntity.failWithoutData("当前数据库内无该别名，请重新输入！").returnResult();
        }
        TimedTaskInfo taskInfo = timedTaskInfoService.searchTaskByAlias(taskAlias);
        boolean success;
//        UserAuthority userAuthority = new UserAuthority(userID, "1000300101");
//        if (userID.equals(taskInfo.getUserID()) || userAuthorityService.searchUserAuthorityByUserId(userID).contains(userAuthority)) {
//            success = timedTaskInfoService.removeTask(taskAlias);
//        } else {
//            LOGGER.warning("用户无该权限");
//            return ResultEntity.failWithoutData("该用户无该权限").returnResult();
//        }
        success = timedTaskInfoService.removeTask(taskAlias);

        // 获取当前时间，用于记录日志
        Date currentTime = new Date();
        // 日期格式
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTimeString = format.format(currentTime);
        TimedTaskLog log = new TimedTaskLog(userID, taskAlias, taskInfo.getTaskName(), taskInfo.getTaskType(),
                taskInfo.getStartTime(), taskInfo.getEndTime(), taskInfo.getCron(), taskInfo.getParams(), "null", currentTimeString, taskInfo.getFlag());

        JobKey jobKey = jobKeyMap.get(taskAlias);
        if (jobKey != null) {
            scheduler.deleteJob(jobKey);
            jobKeyMap.remove(taskAlias);
            LOGGER.info("任务" + taskAlias + "删除成功！(任务列表)");
        }
        log.setFlag("0");
        log.setMsgInfo("删除成功");
        timedTaskLogMapper.insertTimedTaskLog(log);
        try {
            deleteLogByAlias(taskAlias);
            log.setMsgInfo("日志删除成功");
        } catch (Exception e) {
            log.setMsgInfo("日志删除失败");
        }
        timedTaskLogMapper.insertTimedTaskLog(log);
        if (success) {
            LOGGER.info("删除任务" + taskAlias + "成功！（数据库）");
        } else {
            LOGGER.warning("删除任务" + taskAlias + "失败！");
            log.setMsgInfo("删除失败");
            timedTaskLogMapper.insertTimedTaskLog(log);
            return ResultEntity.failWithoutData("删除任务" + taskAlias + "失败！").returnResult();
        }
        return ResultEntity.successWithoutData().returnResult();

    }

    // @Access(level = AccessLevel.updateTask)
    @PostMapping("/updateTask")
    public String updateTask(@RequestParam(value = "userID", defaultValue = "") String userID,
                             @RequestParam(value = "taskAlias", defaultValue = "") String taskAlias,
                             @RequestParam(value = "cron", defaultValue = "") String cron,
                             @RequestParam(value = "startTime", defaultValue = "") String startTimeString,
                             @RequestParam(value = "endTime", defaultValue = "") String endTimeString
    ) throws JsonProcessingException, SchedulerException {

        // 如果参数存在，则对参数进行修剪，除去前后的空格
        cron = (cron == null ? null : cron.trim());
        taskAlias = (taskAlias == null ? null : taskAlias.trim());
        userID = (userID == null ? null : userID.trim());

        if (StringUtil.isBlank(taskAlias)) {
            return ResultEntity.failWithoutData("任务别名为空，请重新输入！").returnResult();
        }
        // 检测userID
        if (!StringUtil.isBlank(userID)) {
            try {
                userID = Integer.parseInt(userID) + "";
            } catch (NumberFormatException e) {
                return ResultEntity.failWithoutData("userID格式有误！").returnResult();
            }
        } else {
            LOGGER.warning("userID为空");
            return ResultEntity.failWithoutData("userID为空").returnResult();
        }

        if (timedTaskInfoService.searchTaskByAlias(taskAlias) == null) {
            return ResultEntity.failWithoutData("当前数据库内无该别名").returnResult();
        }

        if (!CronExpression.isValidExpression(cron)) {
            return ResultEntity.failWithoutData("Cron表达式不合法!").returnResult();
        }

        TimedTaskInfo info = timedTaskInfoService.searchTaskByAlias(taskAlias);

//        UserAuthority userAuthority = new UserAuthority(userID, "1000300101");
//        if (userID.equals(info.getUserID()) || userAuthorityService.searchUserAuthorityByUserId(userID).contains(userAuthority)) {
//            stopTask(userID, taskAlias);
//        } else {
//            LOGGER.warning("用户无该权限");
//            return ResultEntity.failWithoutData("该用户无该权限").returnResult();
//        }
        stopTask(userID, taskAlias);
        if (!cron.equals("")) {
            info.setCron(cron);
        }
        if (!startTimeString.equals("")) {
            info.setStartTime(startTimeString);
        }
        if (!endTimeString.equals("")) {
            info.setEndTime(endTimeString);
        }
        info.setFlag("0");

        timedTaskInfoService.updateTask(info);
        LOGGER.info("任务" + taskAlias + "更新成功！");
        // 获取当前时间，用于记录日志
        Date currentTime = new Date();
        // 日期格式
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTimeString = format.format(currentTime);
        TimedTaskLog log = new TimedTaskLog(userID, taskAlias, info.getTaskName(), info.getTaskType(),
                info.getStartTime(), info.getEndTime(), info.getCron(), info.getParams(), "null", currentTimeString, info.getFlag());
        log.setMsgInfo("更新成功");
        timedTaskLogService.addTimedTaskLog(log);
        return ResultEntity.successWithoutData().returnResult();

    }

    // @Access(level = AccessLevel.retrieveAllTask)
    @PostMapping("/retrieveAllTasks")
    public String retrieveAllTasks() throws JsonProcessingException {
        List<TimedTaskInfo> tasks = timedTaskInfoService.searchAllTask();
        return ResultEntity.successWithData(tasks).returnResult();
    }

    @PostMapping("/retrieveTaskByAlias")
    public String retrieveTaskByAlias(@RequestParam(value = "taskAlias", defaultValue = "") String taskAlias) throws JsonProcessingException {
        if (taskAlias.equals("")) {
            return ResultEntity.failWithoutData("任务别名为空").returnResult();
        }
        TimedTaskInfo task = timedTaskInfoService.searchTaskByAlias(taskAlias);
        if (task == null) {
            return ResultEntity.failWithoutData("该任务名无对应任务").returnResult();
        } else {
            return ResultEntity.successWithData(task).returnResult();
        }
    }

    @PostMapping("/retrieveLogs")
    public String retrieveLogs(@RequestParam(value = "taskName", defaultValue = "") String taskName,
                               @RequestParam(value = "taskAlias", defaultValue = "") String taskAlias,
                               @RequestParam(value = "pageNum", defaultValue = "") String pageNum,
                               @RequestParam(value = "rowNum", defaultValue = "") String rowNum,
                               @RequestParam(value = "recentCount", defaultValue = "") String recentCount) throws JsonProcessingException {

        taskName = (taskName == null ? null : taskName.trim());
        taskAlias = (taskAlias == null ? null : taskAlias.trim());
        pageNum = (pageNum == null ? null : pageNum.trim());
        rowNum = (rowNum == null ? null : rowNum.trim());
        recentCount = (recentCount == null ? null : recentCount.trim());

        if (StringUtil.isEmpty(taskAlias) || StringUtil.isEmpty(taskName)) {
            if( StringUtil.isEmpty(recentCount)) {
                return ResultEntity.failWithoutData("任务名或任务别名为空").returnResult();
            }
        }
        if (StringUtil.isEmpty(pageNum) || StringUtil.isEmpty(rowNum)) {
            return ResultEntity.failWithoutData("页号或条数为空").returnResult();
        }

        Pair<Integer, List<TimedTaskLog>> pair;
        List<TimedTaskLog> timedTaskLogs;
        int size = 0;
        if (StringUtil.isEmpty(recentCount)) {
            pair = timedTaskLogService.searchAllLogs(taskName, taskAlias, pageNum, rowNum);
            timedTaskLogs = pair.getValue1();
            size = pair.getValue0();
        } else {
            try {
                int intCount = Integer.parseInt(recentCount);
                // timedTaskLogs = timedTaskLogService.searchLogByRecordCount(taskName, taskAlias, pageNum, rowNum, Integer.parseInt(recentCount));
                pair = timedTaskLogService.searchLogByRecordCount(pageNum, rowNum, intCount);
                timedTaskLogs = pair.getValue1();
                size = pair.getValue0();
                if (intCount != 0) {
                    size = intCount;
                }
            } catch (NumberFormatException e) {
                return ResultEntity.failWithoutData("入参错误！").returnResult();
            }
        }
//        int size = timedTaskLogMapper.selectLastedTimedTaskLog(taskName, taskAlias).size();
        return ResultEntity.successWithDataMsg(String.valueOf(size), timedTaskLogs).returnResult();
    }

    @PostMapping("/deleteLogByAlias")
    public String deleteLogByAlias(@RequestParam(value = "taskAlias", defaultValue = "") String taskAlias) throws JsonProcessingException {
        taskAlias = (taskAlias == null ? null : taskAlias.trim());
        if (taskAlias.equals("")) {
            return ResultEntity.failWithoutData("任务别名为空，请重新输入").returnResult();
        }
        Boolean success = timedTaskLogService.removeLogByAlias(taskAlias);
        if (success) {
            return ResultEntity.successWithoutData().returnResult();
        } else {
            return ResultEntity.failWithoutData("数据未删除或该任务别名无日志！").returnResult();
        }
    }

    @PostMapping("/deleteLogs")
    public String deleteLogs(@RequestParam(value = "oldestCount", defaultValue = "") String oldestCount) throws JsonProcessingException {
        boolean success;
        if (StringUtil.isEmpty(oldestCount)) {
            success = timedTaskLogService.removeLogs(null);
        } else {
            try {
                success = timedTaskLogService.removeLogs(Integer.parseInt(oldestCount));
            } catch (NumberFormatException e) {
                return ResultEntity.failWithoutData("入参错误！").returnResult();
            }
        }
        if (success) {
            return ResultEntity.successWithoutData().returnResult();
        } else {
            return ResultEntity.failWithoutData("删除失败！").returnResult();
        }
    }


    private boolean writeLog(TimedTaskLog log, String msgInfo) {
        log.setMsgInfo(msgInfo);
        return timedTaskLogService.addTimedTaskLog(log);
    }

    private String returnFailMsg(TimedTaskLog log, String msgInfo) throws JsonProcessingException {
        boolean writeSuccess = writeLog(log, msgInfo);
        if (writeSuccess) {
            LOGGER.log(Level.INFO, "日志成功写入数据库！");
        } else {
            LOGGER.log(Level.WARNING, "日志写入数据库失败！");
        }
        return ResultEntity.failWithoutData(msgInfo).returnResult();
    }
}
