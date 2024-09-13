package com.jnu.tool.task.timed.service.api;

import com.jnu.tool.task.timed.pojo.TimedTaskLog;
import org.javatuples.Pair;

import java.util.List;

/**
 * @author duya001
 */
public interface TimedTaskLogService {
    /**
     * 添加一条定时任务日志记录
     * @param timedTaskLog 日志对象
     * @return 添加成功标志，成功返回true，失败返回false
     */
    boolean addTimedTaskLog(TimedTaskLog timedTaskLog);

    /**
     * 查询全部日志
     * @return size日志总量,TimedTaskLog对象列表
     */
    Pair<Integer, List<TimedTaskLog>> searchAllLogs(String taskName, String taskAlias, String pageNum, String rowNum);

    /**
     * 查询最近产生的recordCount条日志
     * @param recordCount 日志数
     * @return TimedTaskLog对象列表
     */
    // List<TimedTaskLog> searchLogByRecordCount(String taskName, String taskAlias, String pageNum, String rowNum, int recordCount);
    Pair<Integer, List<TimedTaskLog>> searchLogByRecordCount(String pageNum, String rowNum,int recordCount);

    /**
     * 删除日志，当oldestCount为空时，删除所有日志
     * @param oldestCount 要删除的最旧的日志条数
     * @return 删除成功标志
     */
    boolean removeLogs(Integer oldestCount);

    /**
     * 删除对应任务别名所有日志
     * @param taskAlias
     * @return
     */
    Boolean removeLogByAlias(String taskAlias);
}
