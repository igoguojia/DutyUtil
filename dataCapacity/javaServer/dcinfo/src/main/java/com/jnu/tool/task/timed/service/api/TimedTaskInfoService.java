package com.jnu.tool.task.timed.service.api;

import com.jnu.tool.task.timed.pojo.TimedTaskInfo;

import java.util.List;

/**
 * @author duya001
 */
public interface TimedTaskInfoService {
    /**
     * 查询全部任务
     * @return 任务列表
     */
    List<TimedTaskInfo> searchAllTask();

    /**
     * 根据任务ID查询任务
     * @param taskAlias 任务ID
     * @return 任务对象
     */
    TimedTaskInfo searchTaskByAlias(String taskAlias);

    /**
     * 添加一个任务注册表记录
     * @param info TimedTaskInfo对象
     * @return 添加成功标志
     */
    boolean addTask(TimedTaskInfo info);

    /**
     * 移除一条注册表记录
     * @param taskAlias 任务ID
     * @return 删除成功标志
     */
    boolean removeTask(String taskAlias);

    /**
     * 修改状态
     * @param taskAlias 任务别名
     * @param flag 启动状态
     * @return
     */
    boolean updateState(String taskAlias, String flag);

    /**
     * 更新任务
     * @param info 任务信息
     * @return
     */
    boolean updateTask(TimedTaskInfo info);

    /**
     * 获取当前启动的任务
     * @return
     */
    List<TimedTaskInfo> searchLiveTask();
}
