package com.jnu.tool.task.timed.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author duya001
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimedTaskInfo {
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
    private String taskName;
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
    private String params;

    /**
     * 判断当前任务是否启动
     */
    private String flag;
}
