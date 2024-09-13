package com.jnu.tool.authority.service.api;

import com.jnu.tool.authority.pojo.Character;

import java.util.List;

/**
 * @author duya001
 */
public interface CharacterService {
    /**
     * 增加一条Character记录
     *
     * @param character Character对象
     * @return 添加成功标志位，成功返回true，失败返回false
     */
    boolean addCharacter(Character character);

    /**
     * 根据ID删除角色
     *
     * @param characterId 角色ID
     * @return 删除成功标志位，成功返回true，失败返回false
     */
    boolean removeCharacterById(String characterId);

    /**
     * 根据ID更新Character记录
     *
     * @param character Character对象
     * @return 更新成功标志位，成功返回true，失败返回false
     */
    boolean changeCharacter(Character character);

    /**
     * 查询全部角色
     *
     * @return 角色对象列表
     */
    List<Character> searchAllCharacters();

    /**
     * 根据ID查询角色，当查询到多条记录时，返回第一条记录
     *
     * @param characterId 角色ID
     * @return 角色对象列表
     */
    Character searchCharacterById(String characterId);
}
