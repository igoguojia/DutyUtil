package com.jnu.tool.authority.mapper;

import com.jnu.tool.authority.pojo.Character;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author duya001
 */
@Mapper
@Repository("characterMapper")
public interface CharacterMapper {
    /**
     * 增加一条Character记录
     *
     * @param character Character对象
     * @return 影响的记录数
     */
    int insertCharacter(Character character);

    /**
     * 根据ID删除角色
     *
     * @param characterId 角色ID
     * @return 影响的记录数
     */
    int deleteCharacterById(@Param("characterId") String characterId);

    /**
     * 根据ID更新Character记录
     *
     * @param character Character对象
     * @return 影响的记录数
     */
    int updateCharacter(Character character);

    /**
     * 根据ID查询角色
     *
     * @param characterId 角色ID
     * @return 角色对象列表
     */
    List<Character> selectCharacterById(@Param("characterId") String characterId);
}
