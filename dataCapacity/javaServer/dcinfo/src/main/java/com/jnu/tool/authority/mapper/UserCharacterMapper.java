package com.jnu.tool.authority.mapper;

import com.jnu.tool.authority.pojo.UserCharacter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author duya001
 */
@Mapper
@Repository("userCharacterMapper")
public interface UserCharacterMapper {
    /**
     * 在USER_CHARACTER表中插入一条记录
     * @param userCharacter UserCharacter对象
     * @return 影响的记录数
     */
    int insertUserCharacter(UserCharacter userCharacter);

    /**
     * 在USER_CHARACTER表中删除一条记录
     * @param userId 用户ID
     * @param characterId 角色ID
     * @return 影响的记录数
     */
    int deleteUserCharacter(@Param("userID") String userId,
                            @Param("characterId") String characterId);

    /**
     * 在USER_CHARACTER表中查询全部记录
     * @return UserCharacter对象列表
     */
    List<UserCharacter> selectAllUserCharacter();

    /**
     * 在USER_CHARACTER表中，根据角色ID查询该角色拥有的所有权限
     * @param userId 用户ID
     * @return UserCharacter对象列表
     */
    List<UserCharacter> selectUserCharacterByUserId(@Param("userID") String userId);

    /**
     * 在USER_CHARACTER表中，根据权限ID查询拥有该权限的所有角色
     * @param characterId 角色ID
     * @return UserCharacter对象列表
     */
    List<UserCharacter> selectUserCharacterByCharacterId(@Param("characterId") String characterId);
}
