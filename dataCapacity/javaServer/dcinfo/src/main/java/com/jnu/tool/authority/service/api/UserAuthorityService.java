package com.jnu.tool.authority.service.api;

import com.jnu.tool.authority.pojo.Authority;
import com.jnu.tool.authority.pojo.UserAuthority;

import java.util.List;

/**
 * @author duya001
 */
public interface UserAuthorityService {
    /**
     * 在USER_AUTHORITY表中插入一条记录
     * @param userAuthority UserCharacter对象
     * @return 添加成功标志，成功返回true，失败返回false
     */
    boolean addUserAuthority(UserAuthority userAuthority);

    /**
     * 在USER_AUTHORITY表中删除一条记录
     * @param userId 用户ID
     * @param authorityId 权限ID
     * @return 影响的记录数
     */
    boolean removeUserAuthority(String userId, String authorityId);

    /**
     * 在USER_AUTHORITY表中查询全部记录
     * @return UserAuthority对象列表
     */
    List<UserAuthority> searchAllUserAuthority();

    /**
     * 在USER_AUTHORITY表中，根据角色ID查询该角色拥有的所有权限
     * @param userId 用户ID
     * @return UserAuthority对象列表
     */
    List<UserAuthority> searchUserAuthorityByUserId(String userId);

    /**
     * 在USER_AUTHORITY表中，根据权限ID查询拥有该权限的所有角色
     * @param authorityId 权限ID
     * @return UserAuthority对象列表
     */
    List<UserAuthority> searchUserAuthorityByAuthorityId(String authorityId);

    /**
     * 查询USER_AUTHORITY表中是否包含指定的记录
     * @param userId 用户ID
     * @param authorityId 权限ID
     * @return 包含标志，包含返回true，不包含返回false
     */
    boolean existUserAuthority(String userId, String authorityId);

    /**
     * 根据用户ID查询所有权限
     * @param userId 用户ID
     * @return Authority对象列表
     */
    List<Authority> searchAllAuthorityByUserId(String userId);

    /**
     * 判断用户是否拥有某权限
     * @param userId 用户ID
     * @param authorityId 权限ID
     * @return 拥有标志位，拥有则返回true，不拥有返回false
     */
    boolean ownAuthority(String userId, String authorityId);
}
