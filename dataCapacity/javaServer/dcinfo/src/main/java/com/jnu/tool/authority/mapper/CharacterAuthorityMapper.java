package com.jnu.tool.authority.mapper;

import com.jnu.tool.authority.pojo.CharacterAuthority;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author duya001
 */
@Mapper
@Repository("characterAuthorityMapper")
public interface CharacterAuthorityMapper {
    /**
     * 在CHARACTER_AUTHORITY表中插入一条记录
     * @param characterAuthority CharacterAuthority对象
     * @return 影响的记录数
     */
    int insertCharacterAuthority(CharacterAuthority characterAuthority);

    /**
     * 在CHARACTER_AUTHORITY表中删除一条记录
     * @param characterId 角色ID
     * @param authorityId 权限ID
     * @return 影响的记录数
     */
    int deleteCharacterAuthority(@Param("characterId") String characterId,
                                 @Param("authorityId") String authorityId);

    /**
     * 在CHARACTER_AUTHORITY表中查询全部记录
     * @return CharacterAuthority对象列表
     */
    List<CharacterAuthority> selectAllCharacterAuthority();

    /**
     * 在CHARACTER_AUTHORITY表中，根据角色ID查询该角色拥有的所有权限
     * @param characterId 角色ID
     * @return CharacterAuthority对象列表
     */
    List<CharacterAuthority> selectCharacterAuthorityByCharacterId(@Param("characterId") String characterId);

    /**
     * 在CHARACTER_AUTHORITY表中，根据权限ID查询拥有该权限的所有角色
     * @param authorityId 权限ID
     * @return CharacterAuthority对象列表
     */
    List<CharacterAuthority> selectCharacterAuthorityByAuthorityId(@Param("authorityId") String authorityId);
}
