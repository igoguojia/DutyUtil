package com.jnu.common.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author duya001
 */
@Mapper
@Repository
public interface TeamMemberMapper {
    // 根据组名选择管理者
    @Select("select EMPLOYEECODE from TEAMMEMBERS  where TEAMLEADER = 1 AND TEAMNAME = #{groupName}")
    List<String> getLeaderIDByGroupName(@Param("groupName") String groupName);

    // 根据前端的userID选择组名，以此验证组名是否存在
    @Select("select teamname from TEAMMEMBERS where employeecode = #{userID}")
    List<String> getTeamNameByUserID(@Param("userID") String userID);
}
