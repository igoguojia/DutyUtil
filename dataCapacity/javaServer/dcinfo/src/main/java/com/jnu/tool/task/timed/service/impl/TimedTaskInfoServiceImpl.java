package com.jnu.tool.task.timed.service.impl;

import com.jnu.tool.task.timed.mapper.TimedTaskInfoMapper;
import com.jnu.tool.task.timed.pojo.TimedTaskInfo;
import com.jnu.tool.task.timed.service.api.TimedTaskInfoService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author duya001
 */
@Scope("prototype")
@Service("timedTaskInfoService")
public class TimedTaskInfoServiceImpl implements TimedTaskInfoService {
    private final TimedTaskInfoMapper timedTaskInfoMapper;

    private static final ReadWriteLock LOCK = new ReentrantReadWriteLock();

    public TimedTaskInfoServiceImpl(@Qualifier("timedTaskInfoMapper") TimedTaskInfoMapper timedTaskInfoMapper) {
        this.timedTaskInfoMapper = timedTaskInfoMapper;
    }

    @Override
    public List<TimedTaskInfo> searchAllTask() {
        LOCK.readLock().lock();
        try {
            return timedTaskInfoMapper.selectAllTask();
        } finally {
            LOCK.readLock().unlock();
        }
    }

    @Override
    public TimedTaskInfo searchTaskByAlias(String taskAlias) {
        LOCK.readLock().lock();
        try {
            List<TimedTaskInfo> infos = timedTaskInfoMapper.selectTaskById(taskAlias);
            if (infos == null || infos.isEmpty()) {
                return null;
            }
            return infos.get(0);
        } finally {
            LOCK.readLock().unlock();
        }
    }

    @Override
    public boolean addTask(TimedTaskInfo info) {
        LOCK.readLock().lock();
        try {
            if (searchTaskByAlias(info.getTaskAlias()) != null) {
                return true;
            }
            return timedTaskInfoMapper.insertTask(info) != 0;
        } finally {
            LOCK.readLock().unlock();
        }
    }

    @Override
    public boolean removeTask(String taskAlias) {
        LOCK.readLock().lock();
        try {
            return timedTaskInfoMapper.deleteTask(taskAlias) != 0;
        } finally {
            LOCK.readLock().unlock();
        }
    }

    @Override
    public boolean updateState(String taskAlias, String flag) {
        LOCK.readLock().lock();
        try {
            return timedTaskInfoMapper.updateState(taskAlias, flag) != 0;
        } finally {
            LOCK.readLock().unlock();
        }    }

    @Override
    public boolean updateTask(TimedTaskInfo info) {
        LOCK.readLock().lock();
        try {
            return timedTaskInfoMapper.updateTask(info) != 0;
        } finally {
            LOCK.readLock().unlock();
        }
    }

    @Override
    public List<TimedTaskInfo> searchLiveTask() {
        LOCK.readLock().lock();
        try {
            return timedTaskInfoMapper.selectLiveTask();
        } finally {
            LOCK.readLock().unlock();
        }    }
}
