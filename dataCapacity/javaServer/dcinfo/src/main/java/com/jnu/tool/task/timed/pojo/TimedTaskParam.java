package com.jnu.tool.task.timed.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author duya001
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimedTaskParam {
    /**
     * 添加任务的用户ID
     */
    private String userID;
    /**
     * 任务ID，用于区别同名任务
     */
    private String taskAlias;
    /**
     * 要执行的方法，形式为全类名+方法名，如"com.jnu.tool.sqltool.controller.SqlToolController.connectTest"
     */
    private String task;
    /**
     * 任务类型，可取的值为“0”和“1”，“0”表示cmd任务，“1”表示方法任务
     */
    private String taskType;
    /**
     * 任务循环开始时间
     */
    private String startTime;
    /**
     * 任务循环结束时间
     */
    private String endTime;
    /**
     * cron表达式，指定执行时间
     */
    private String cron;
    /**
     * 执行方法时，如果需要参数，则传入对应的参数
     */
    private List<Map<String, Object>> params;

    private String flag;

    public TimedTaskParam(TimedTaskInfo info, List<Map<String, Object>> params) {
        this.userID = info.getUserID();
        this.task = info.getTaskName();
        this.taskAlias = info.getTaskAlias();
        this.taskType = info.getTaskType();
        this.startTime = info.getStartTime();
        this.endTime = info.getEndTime();
        this.cron = info.getCron();
        this.params = params;
        this.flag = info.getFlag();
    }
}
