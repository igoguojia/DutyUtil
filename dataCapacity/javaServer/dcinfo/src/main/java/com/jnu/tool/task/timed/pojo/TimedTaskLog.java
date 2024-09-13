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
public class TimedTaskLog {
    private String userID;
    private String taskAlias;
    private String taskName;
    private String taskType;
    private String startTime;
    private String endTime;
    private String cronExpr;
    private String methodParam;
    private String msgInfo;
    private String submitTime;
    private String flag;
}
