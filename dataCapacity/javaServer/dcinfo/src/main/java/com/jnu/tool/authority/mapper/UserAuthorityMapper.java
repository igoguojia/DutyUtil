package com.jnu.tool.authority.mapper;

import com.jnu.tool.authority.pojo.Authority;
import com.jnu.tool.authority.pojo.UserAuthority;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author duya001
 */
@Mapper
@Repository("userAuthorityMapper")
public interface UserAuthorityMapper {
    /**
     * 在USER_AUTHORITY表中插入一条记录
     * @param userAuthority UserCharacter对象
     * @return 影响的记录数
     */
    int insertUserAuthority(UserAuthority userAuthority);

    /**
     * 在USER_AUTHORITY表中删除一条记录
     * @param userId 用户ID
     * @param authorityId 权限ID
     * @return 影响的记录数
     */
    int deleteUserAuthority(@Param("userID") String userId,
                            @Param("authorityId") String authorityId);

    /**
     * 在USER_AUTHORITY表中查询全部记录
     * @return UserAuthority对象列表
     */
    List<UserAuthority> selectAllUserAuthority();

    /**
     * 在USER_AUTHORITY表中，根据角色ID查询该角色拥有的所有权限
     * @param userId 用户ID
     * @return UserAuthority对象列表
     */
    List<UserAuthority> selectUserAuthorityByUserId(@Param("userID") String userId);

    /**
     * 在USER_AUTHORITY表中，根据权限ID查询拥有该权限的所有角色
     * @param authorityId 权限ID
     * @return UserAuthority对象列表
     */
    List<UserAuthority> selectUserAuthorityByAuthorityId(@Param("authorityId") String authorityId);

    /**
     * 查询一个用户的所有权限（包括特殊权限）
     * @param userId 用户权限
     * @return Authority对象列表
     */
    List<Authority> selectAllAuthorityByUserId(@Param("userID") String userId);
}
