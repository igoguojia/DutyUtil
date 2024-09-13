package com.jnu.tool.authority.service.api;

import com.jnu.tool.authority.pojo.UserCharacter;

import java.util.List;

/**
 * @author duya001
 */
public interface UserCharacterService {
    /**
     * 在USER_CHARACTER表中插入一条记录
     * @param userCharacter UserCharacter对象
     * @return 添加成功标志，成功返回true，失败返回false
     */
    boolean addUserCharacter(UserCharacter userCharacter);

    /**
     * 在USER_CHARACTER表中删除一条记录
     * @param userId 用户ID
     * @param characterId 角色ID
     * @return 删除成功标志，成功返回true，失败返回false
     */
    boolean deleteUserCharacter(String userId, String characterId);

    /**
     * 在USER_CHARACTER表中查询全部记录
     * @return UserCharacter对象列表
     */
    List<UserCharacter> searchAllUserCharacter();

    /**
     * 在USER_CHARACTER表中，根据角色ID查询该角色拥有的所有权限
     * @param userId 用户ID
     * @return UserCharacter对象列表
     */
    List<UserCharacter> searchUserCharacterByUserId(String userId);

    /**
     * 在USER_CHARACTER表中，根据权限ID查询拥有该权限的所有角色
     * @param characterId 角色ID
     * @return UserCharacter对象列表
     */
    List<UserCharacter> searchUserCharacterByCharacterId(String characterId);

    /**
     * 判断表中USER_CHARACTER是否含有指定的记录
     * @param userId 用户ID
     * @param characterId 角色ID
     * @return 包含标志位，包含返回true，不包含返回false
     */
    boolean existUserCharacter(String userId, String characterId);
}
