package com.jnu.tool.authority.mapper;


import com.jnu.tool.authority.pojo.Group;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository("groupMapper")
public interface GroupMapper {
    /**
     * 增加一条Group记录
     *
     * @param group Group对象
     * @return 影响的记录数
     */
    int insertGroup(Group group);

    /**
     * 根据Name删除组别
     *
     * @param groupName 组别Name
     * @return 影响的记录数
     */
    int deleteGroupByName(@Param("groupName") String groupName);

    /**
     * 根据Name更新Group记录
     *
     * @param group Group对象
     * @return 影响的记录数
     */
    int updateGroup(Group group);

    /**
     * 根据Name查询组别
     *
     * @param groupName 组别Name
     * @return 组别对象列表
     */
    List<Group> selectGroupByName(@Param("groupName") String groupName);
}
