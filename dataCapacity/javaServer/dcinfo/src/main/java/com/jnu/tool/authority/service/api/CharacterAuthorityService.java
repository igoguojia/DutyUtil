package com.jnu.tool.authority.service.api;

import com.jnu.tool.authority.pojo.CharacterAuthority;

import java.util.List;

/**
 * @author duya001
 */
public interface CharacterAuthorityService {
    /**
     * 在CHARACTER_AUTHORITY表中插入一条记录
     * @param characterAuthority CharacterAuthority对象
     * @return 添加成功标志，成功返回true，失败返回false
     */
    boolean addCharacterAuthority(CharacterAuthority characterAuthority);

    /**
     * 在CHARACTER_AUTHORITY表中删除一条记录
     * @param characterId 角色ID
     * @param authorityId 权限ID
     * @return 删除成功标志，成功返回true，失败返回false
     */
    boolean removeCharacterAuthority(String characterId, String authorityId);

    /**
     * 在CHARACTER_AUTHORITY表中查询全部记录
     * @return CharacterAuthority对象列表
     */
    List<CharacterAuthority> searchAllCharacterAuthority();

    /**
     * 在CHARACTER_AUTHORITY表中，根据角色ID查询该角色拥有的所有权限
     * @param characterId 角色ID
     * @return CharacterAuthority对象列表
     */
    List<CharacterAuthority> searchCharacterAuthorityByCharacterId(String characterId);

    /**
     * 在CHARACTER_AUTHORITY表中，根据权限ID查询拥有该权限的所有角色
     * @param authorityId 权限ID
     * @return CharacterAuthority对象列表
     */
    List<CharacterAuthority> searchCharacterAuthorityByAuthorityId(String authorityId);

    /**
     * 判断是否存在指定的记录
     * @param characterId 角色ID
     * @param authorityId 权限ID
     * @return 记录存在标志，存在返回true，不存在返回false
     */
    boolean existCharacterAuthority(String characterId, String authorityId);
}
