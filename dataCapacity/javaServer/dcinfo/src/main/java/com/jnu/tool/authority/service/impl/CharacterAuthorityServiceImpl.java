package com.jnu.tool.authority.service.impl;

import com.iceolive.util.StringUtil;
import com.jnu.tool.authority.mapper.CharacterAuthorityMapper;
import com.jnu.tool.authority.pojo.CharacterAuthority;
import com.jnu.tool.authority.service.api.CharacterAuthorityService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author duya001
 */
@Service("characterAuthorityService")
public class CharacterAuthorityServiceImpl implements CharacterAuthorityService {
    private final CharacterAuthorityMapper characterAuthorityMapper;

    public CharacterAuthorityServiceImpl(@Qualifier("characterAuthorityMapper") CharacterAuthorityMapper characterAuthorityMapper) {
        this.characterAuthorityMapper = characterAuthorityMapper;
    }

    @Override
    public boolean addCharacterAuthority(CharacterAuthority characterAuthority) {
        if (StringUtil.isBlank(characterAuthority.getCharacterId()) || StringUtil.isBlank(characterAuthority.getAuthorityId())) {
            return false;
        }
        return characterAuthorityMapper.insertCharacterAuthority(characterAuthority) != 0;
    }

    @Override
    public boolean removeCharacterAuthority(String characterId, String authorityId) {
        if (StringUtil.isBlank(characterId) || StringUtil.isBlank(authorityId)) {
            return false;
        }
        return characterAuthorityMapper.deleteCharacterAuthority(characterId, authorityId) != 0;
    }

    @Override
    public List<CharacterAuthority> searchAllCharacterAuthority() {
        return characterAuthorityMapper.selectAllCharacterAuthority();
    }

    @Override
    public List<CharacterAuthority> searchCharacterAuthorityByCharacterId(String characterId) {
        if (StringUtil.isBlank(characterId)) {
            return null;
        }
        return characterAuthorityMapper.selectCharacterAuthorityByCharacterId(characterId);
    }

    @Override
    public List<CharacterAuthority> searchCharacterAuthorityByAuthorityId(String authorityId) {
        if (StringUtil.isBlank(authorityId)) {
            return null;
        }
        return characterAuthorityMapper.selectCharacterAuthorityByAuthorityId(authorityId);
    }

    @Override
    public boolean existCharacterAuthority(String characterId, String authorityId) {
        if (StringUtil.isBlank(characterId) || StringUtil.isBlank(authorityId)) {
            return false;
        }
        List<CharacterAuthority> list = characterAuthorityMapper.selectCharacterAuthorityByCharacterId(characterId);
        List<CharacterAuthority> collect = list.stream().filter((element) -> authorityId.equals(element.getAuthorityId())).collect(Collectors.toList());
        return !collect.isEmpty();
    }
}
