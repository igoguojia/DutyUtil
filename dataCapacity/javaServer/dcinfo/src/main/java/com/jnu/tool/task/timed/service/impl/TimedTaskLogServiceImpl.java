package com.jnu.tool.task.timed.service.impl;

import com.jnu.tool.task.timed.mapper.TimedTaskLogMapper;
import com.jnu.tool.task.timed.pojo.TimedTaskLog;
import com.jnu.tool.task.timed.service.api.TimedTaskLogService;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author duya001
 */
@Service("timedTaskLogService")
public class TimedTaskLogServiceImpl implements TimedTaskLogService {
    private final TimedTaskLogMapper timedTaskLogMapper;

    public TimedTaskLogServiceImpl(@Qualifier("timedTaskLogMapper") TimedTaskLogMapper timedTaskLogMapper) {
        this.timedTaskLogMapper = timedTaskLogMapper;
    }

    @Override
    public boolean addTimedTaskLog(TimedTaskLog timedTaskLog) {
        if (timedTaskLog == null) {
            return false;
        }
        return timedTaskLogMapper.insertTimedTaskLog(timedTaskLog) != 0;
    }

    @Override
    public Pair<Integer, List<TimedTaskLog>> searchAllLogs(String taskName, String taskAlias, String pageNum, String rowNum) {
        // List<TimedTaskLog> timedTaskLogs = timedTaskLogMapper.selectLastedTimedTaskLog(taskName, taskAlias, null);
        List<TimedTaskLog> timedTaskLogs = timedTaskLogMapper.selectLastedTimedTaskLog(taskName, taskAlias);
        int size=timedTaskLogs.size();
        int pageNumInt = Integer.parseInt(pageNum);
        int rowNumInt = Integer.parseInt(rowNum);

        if ((pageNumInt - 1) * rowNumInt > size) {
            return null;
        }

        if (pageNumInt * rowNumInt > size){
            timedTaskLogs = timedTaskLogs.subList(rowNumInt * (pageNumInt - 1), size);
        } else {
            timedTaskLogs = timedTaskLogs.subList(rowNumInt * (pageNumInt - 1), rowNumInt * pageNumInt);
        }
        return Pair.with(size, timedTaskLogs);
    }

    @Override
    // public List<TimedTaskLog> searchLogByRecordCount(String taskName, String taskAlias, String pageNum, String rowNum, int recordCount) {
    public Pair<Integer, List<TimedTaskLog>> searchLogByRecordCount(String pageNum, String rowNum,int recordCount) {
        List<TimedTaskLog> timedTaskLogs = timedTaskLogMapper.selectRecentTimedTaskLog(recordCount);
        int size=timedTaskLogs.size();
        int pageNumInt = Integer.parseInt(pageNum);
        int rowNumInt = Integer.parseInt(rowNum);

        if ((pageNumInt - 1) * rowNumInt > size) {
            return Pair.with(size, timedTaskLogs);
        }

        if (pageNumInt * rowNumInt > size){
            timedTaskLogs = timedTaskLogs.subList(rowNumInt * (pageNumInt - 1), size);
        } else {
            timedTaskLogs = timedTaskLogs.subList(rowNumInt * (pageNumInt - 1), rowNumInt * pageNumInt);
        }
        return Pair.with(size, timedTaskLogs);
    }

    @Override
    public boolean removeLogs(Integer oldestCount) {
        return timedTaskLogMapper.deleteTimedTaskLogs(oldestCount) != 0;
    }

    @Override
    public Boolean removeLogByAlias(String taskAlias) {
        return timedTaskLogMapper.removeLogByAlias(taskAlias) != 0;
    }
}
