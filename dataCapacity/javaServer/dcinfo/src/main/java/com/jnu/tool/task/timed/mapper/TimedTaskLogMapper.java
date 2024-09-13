package com.jnu.tool.task.timed.mapper;

import com.jnu.tool.task.timed.pojo.TimedTaskLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author duya001
 */
@Mapper
@Repository("timedTaskLogMapper")
public interface TimedTaskLogMapper {
    /**
     * 添加一条定时任务日志记录
     * @param timedTaskLog 日志对象
     * @return 影响的记录数
     */
    int insertTimedTaskLog(TimedTaskLog timedTaskLog);

    /**
     * 查询指定任务的所有日志
     * @param taskAlias 任务别名
     * @param taskName 任务名字
     * @return TimedTaskLog对象列表
     */
    // List<TimedTaskLog> selectLastedTimedTaskLog(@Param("taskName") String taskName, @Param("taskAlias") String taskAlias, @Param("recordCount") Integer recordCount);
    List<TimedTaskLog> selectLastedTimedTaskLog(@Param("taskName") String taskName, @Param("taskAlias") String taskAlias);
    
    /**
     * 查询最近产生的recordCount条日志
     * @param recordCount 日志数
     * @return TimedTaskLog对象列表
     */
    List<TimedTaskLog> selectRecentTimedTaskLog(@Param("recordCount") Integer recordCount);

    /**
     * 删除日志，当oldestCount为空时，删除所有日志
     * @param oldestCount 要删除的最旧的日志条数
     * @return 影响的记录数
     */
    int deleteTimedTaskLogs(@Param("oldestCount") Integer oldestCount);

    /**
     * 删除对应任务别名所有日志
     * @param taskAlias 任务别名
     * @return
     */
    int removeLogByAlias(String taskAlias);
}
