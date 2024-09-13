package com.jnu.tool.authority.service.api;

import com.jnu.tool.authority.pojo.Group;

import java.util.List;

public interface GroupService {
    /**
     * 增加一条Group记录
     *
     * @param group Group对象
     * @return 添加成功标志位，成功返回true，失败返回false
     */
    boolean addGroup(Group group);

    /**
     * 根据Name删除组别
     *
     * @param groupName 组别Name
     * @return 删除成功标志位，成功返回true，失败返回false
     */
    boolean removeGroupByName(String groupName);

    /**
     * 根据Name更新Group记录
     *
     * @param group Group对象
     * @return 更新成功标志位，成功返回true，失败返回false
     */
    boolean changeGroup(Group group);

    /**
     * 查询全部组别
     *
     * @return 组别对象列表
     */
    List<Group> searchAllGroups();

    /**
     * 根据Name查询组别，当查询到多条记录时，返回第一条记录
     *
     * @param groupName 组别Name
     * @return 组别对象列表
     */
    Group searchGroupByName(String groupName);
}
