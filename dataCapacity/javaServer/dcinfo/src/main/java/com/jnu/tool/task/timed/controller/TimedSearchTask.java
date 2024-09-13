package com.jnu.tool.task.timed.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iceolive.util.StringUtil;
import com.jnu.tool.task.timed.pojo.TimedTaskInfo;
import com.jnu.tool.task.timed.pojo.TimedTaskParam;
import com.jnu.tool.task.timed.service.api.TimedTaskInfoService;
import com.jnu.tool.task.timed.util.QuartzUtil;
import com.jnu.tool.variable.pojo.Variable;
import com.jnu.tool.variable.service.api.VariableService;
import org.quartz.SchedulerException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @author duya001
 */
@Component
@EnableAsync
public class TimedSearchTask implements ApplicationRunner {
    private final TimedTaskInfoService timedTaskInfoService;
    private final TimedTaskController timedTaskController;
    private final VariableService variableService;
    private final QuartzUtil quartzUtil;

    private static final Logger LOGGER = Logger.getLogger("com.jnu.tool.task.timed.controller.TimedSearchTask");

    private final List<TimedTaskInfo> lastTasks = new ArrayList<>(10);

    /**
     * 当前查询后，要添加的任务
     */
    List<TimedTaskInfo> addTasks = new ArrayList<>(10);
    /**
     * 当前查询后，要修改的任务
     */
    List<TimedTaskInfo> changeTasks = new ArrayList<>(10);
    /**
     * 当前查询后，要删除的任务
     */
    List<TimedTaskInfo> deleteTasks = new ArrayList<>(10);

    List<TimedTaskInfo> startTasks = new ArrayList<>(10);

    List<TimedTaskInfo> pauseTasks = new ArrayList<>(10);

    public TimedSearchTask(TimedTaskInfoService timedTaskInfoService, TimedTaskController timedTaskController, VariableService variableService, QuartzUtil quartzUtil) {
        this.timedTaskInfoService = timedTaskInfoService;
        this.timedTaskController = timedTaskController;
        this.variableService = variableService;
        this.quartzUtil = quartzUtil;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        while (true) {
            // 查询数据库中全部任务
            List<TimedTaskInfo> tasks = timedTaskInfoService.searchAllTask();
            // 对任务做出处理
            assignTask(tasks);
            // 动态获取要等待的时间
            Variable variable = variableService.searchVariableByName("timedTask_readDB_timeInterval");
            int interval = Integer.parseInt(variable.getVariableValue());
            TimeUnit.SECONDS.sleep(interval);
        }
    }

    /**
     * 自动根据目前查询到的记录，做出处理
     *
     */
    private void assignTask(List<TimedTaskInfo> tasks) {
        //系统启动
        if (lastTasks.isEmpty()) {
            for (TimedTaskInfo task : tasks) {
                add(task);
                if("0".equals(task.getFlag())){
                    pause(task);
                }
            }
            lastTasks.clear();
            lastTasks.addAll(tasks);
        } else {
            // 获取对应的队列
            getQueue(tasks);
            // 用多线程执行操作
            deleteTasks(deleteTasks);
            changeTasks(changeTasks);
            addTasks(addTasks);
            pauseTasks(pauseTasks);
            startTasks(startTasks);
            this.addTasks.clear();
            this.deleteTasks.clear();
            this.changeTasks.clear();
            this.pauseTasks.clear();
            this.startTasks.clear();
        }
//        // 获取对应的队列
//        getQueue(tasks);
//        // 用多线程执行操作
//        deleteTasks(deleteTasks);
//        changeTasks(changeTasks);
//        addTasks(addTasks);
//        pauseTasks(pauseTasks);
//        startTasks(startTasks);
//        this.addTasks.clear();
//        this.deleteTasks.clear();
//        this.changeTasks.clear();
//        this.pauseTasks.clear();
//        this.startTasks.clear();
    }

    /**
     * 初始化三个队列
     *
     */
    private void getQueue(List<TimedTaskInfo> tasks) {
//        if (lastTasks.isEmpty()) {
//            this.addTasks = tasks;
//        } else {
//            for (TimedTaskInfo task : tasks) {
//                int contains = contains(task, lastTasks);
//                // 遍历查询到的每一个任务
//                if (contains == -1) {
//                    // 如果上一次查询状态的任务队列中不包含该任务，就添加到添加队列中
//                    addTasks.add(task);
//                } else if (contains == 1) {
//                    // 如果修改过，添加新任务到修改队列中
//                    changeTasks.add(task);
//                } else if (contains == 2) {
//                    // 启动
//                    startTasks.add(task);
//                } else if (contains == -2) {
//                    //暂停
//                    pauseTasks.add(task);
//                }
//            }
//            for (TimedTaskInfo lastTask : lastTasks) {
//                int contains = contains(lastTask, tasks);
//                // 遍历上一次查询状态的每一个任务
//                if (contains == -1) {
//                    // 如果本次查询状态中不包含上一次的任务，则将该任务添加到删除队列
//                    deleteTasks.add(lastTask);
//                }
//            }
//        }
        for (TimedTaskInfo task : tasks) {
            int contains = contains(task, lastTasks);
            // 遍历查询到的每一个任务
            if (contains == -1) {
                // 如果上一次查询状态的任务队列中不包含该任务，就添加到添加队列中
                addTasks.add(task);
            } else if (contains == 1) {
                // 如果修改过，添加新任务到修改队列中
                changeTasks.add(task);
            } else if (contains == 2) {
                // 启动
                startTasks.add(task);
            } else if (contains == -2) {
                //暂停
                pauseTasks.add(task);
            }
        }
        for (TimedTaskInfo lastTask : lastTasks) {
            int contains = contains(lastTask, tasks);
            // 遍历上一次查询状态的每一个任务
            if (contains == -1) {
                // 如果本次查询状态中不包含上一次的任务，则将该任务添加到删除队列
                deleteTasks.add(lastTask);
            }
        }
        lastTasks.clear();
        lastTasks.addAll(tasks);
    }

    @Async
    void addTasks(List<TimedTaskInfo> tasks) {
        for (TimedTaskInfo task : tasks) {
            add(task);
        }
    }

    @Async
    void changeTasks(List<TimedTaskInfo> tasks) {
        for (TimedTaskInfo task : tasks) {
            delete(task);
            add(task);
            pause(task);
        }
    }

    @Async
    void deleteTasks(List<TimedTaskInfo> tasks) {
        for (TimedTaskInfo task : tasks) {
            delete(task);
        }
    }

    @Async
    void pauseTasks(List<TimedTaskInfo> tasks) {
        for (TimedTaskInfo task : tasks) {
            pause(task);
        }
    }

    @Async
    void startTasks(List<TimedTaskInfo> tasks) {
        for (TimedTaskInfo task : tasks) {
            start(task);
        }
    }

    private void pause(TimedTaskInfo task) {
        String taskAlias = task.getTaskAlias();
        try {
            quartzUtil.pauseTask(taskAlias);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    private void start(TimedTaskInfo task) {
        String taskAlias = task.getTaskAlias();
        try {
            quartzUtil.startTask(taskAlias);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void add(TimedTaskInfo task) {
        ObjectMapper objectMapper = new ObjectMapper();
        String params = task.getParams();
        List<Map<String, Object>> list = null;
        if (!StringUtil.isBlank(params)) {
            try {
//                list = objectMapper.readValue(params, List.class);
                list = objectMapper.readValue(params,
                        new TypeReference<List<Map<String, Object>>>() {
                        });
            } catch (JsonProcessingException e) {
                LOGGER.warning(task.getTaskAlias() + "任务参数异常！");
            }
        }
        TimedTaskParam timedTaskParam = new TimedTaskParam(task, list);
        try {
            quartzUtil.createTask(timedTaskParam);
            LOGGER.info("任务" + timedTaskParam.getTaskAlias() + "创建成功！");
        } catch (JsonProcessingException | ParseException e) {
            LOGGER.warning("任务" + task.getTaskAlias() + "创建失败！");
        }
    }

    private void delete(TimedTaskInfo task) {
        String taskAlias = task.getTaskAlias();
        try {
            quartzUtil.deleteTask(taskAlias);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据任务ID检测tasks是否包含task
     */
    private int contains(TimedTaskInfo task, List<TimedTaskInfo> tasks) {
        for (TimedTaskInfo timedTaskInfo : tasks) {
            if (task.getTaskAlias().equals(timedTaskInfo.getTaskAlias()) && task.equals(timedTaskInfo)) {
                return 0; // 不变
            } else if (task.getTaskAlias().equals(timedTaskInfo.getTaskAlias()) && !task.equals(timedTaskInfo)) {
                if (task.getTaskName().equals(timedTaskInfo.getTaskName()) && task.getTaskType().equals(timedTaskInfo.getTaskType())
                        && task.getStartTime().equals(timedTaskInfo.getStartTime()) && task.getEndTime().equals(timedTaskInfo.getEndTime())
                        && task.getCron().equals(timedTaskInfo.getCron()) && task.getParams().equals(timedTaskInfo.getParams())) {
                    if ("1".equals(task.getFlag())) {
                        return 2; // 启动
                    } else if ("0".equals(task.getFlag())) {
                        return -2; // 暂停
                    }
                } else {
                    return 1; // 修改
                }
            }
        }
        return -1; //新增
    }
}
