package com.jnu.tool.task.timed.mapper;

import com.jnu.tool.task.timed.pojo.TimedTaskInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author duya001
 */
@Mapper
@Repository("timedTaskInfoMapper")
public interface TimedTaskInfoMapper {
    /**
     * 查询全部任务
     * @return 任务列表
     */
    List<TimedTaskInfo> selectAllTask();

    /**
     * 根据任务ID查询任务信息
     * @param taskAlias 任务ID
     * @return 任务列表
     */
    List<TimedTaskInfo> selectTaskById(@Param("taskAlias") String taskAlias);

    /**
     * 添加一个任务注册表记录
     * @param timedTaskInfo TimedTaskInfo对象
     * @return 影响的记录数
     */
    int insertTask(TimedTaskInfo timedTaskInfo);

    /**
     * 移除一条注册表记录
     * @param taskAlias 任务ID
     * @return 影响的记录数
     */
    int deleteTask(@Param("taskAlias") String taskAlias);

    /**
     * 更新一条任务记录
     * @param timedTaskInfo 任务
     * @return 影响的记录数
     */
    int updateTask(TimedTaskInfo timedTaskInfo);

    /**
     * 更新任务状态
     * @param taskAlias 任务别名
     * @return 影响的记录数
     */
    int updateState(@Param("taskAlias") String taskAlias, @Param("flag") String flag);

    /**
     * 获取当前启动的任务
     * @return
     */
    List<TimedTaskInfo> selectLiveTask();
}
