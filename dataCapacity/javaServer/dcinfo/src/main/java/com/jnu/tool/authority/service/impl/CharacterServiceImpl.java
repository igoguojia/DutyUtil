package com.jnu.tool.authority.service.impl;

import com.iceolive.util.StringUtil;
import com.jnu.tool.authority.mapper.CharacterMapper;
import com.jnu.tool.authority.pojo.Character;
import com.jnu.tool.authority.service.api.CharacterService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author duya001
 */
@Service("characterService")
public class CharacterServiceImpl implements CharacterService {
    private final CharacterMapper characterMapper;

    public CharacterServiceImpl(@Qualifier("characterMapper") CharacterMapper characterMapper) {
        this.characterMapper = characterMapper;
    }

    @Override
    public boolean addCharacter(Character character) {
        if (StringUtil.isBlank(character.getCharacterId()) || StringUtil.isBlank(character.getCharacterDescription())) {
            return false;
        }
        return characterMapper.insertCharacter(character) != 0;
    }

    @Override
    public boolean removeCharacterById(String characterId) {
        if (StringUtil.isBlank(characterId)) {
            return false;
        }
        return characterMapper.deleteCharacterById(characterId) != 0;
    }

    @Override
    public boolean changeCharacter(Character character) {
        if (StringUtil.isBlank(character.getCharacterId()) || StringUtil.isBlank(character.getCharacterDescription())) {
            return false;
        }
        return characterMapper.updateCharacter(character) != 0;
    }

    @Override
    public List<Character> searchAllCharacters() {
        return characterMapper.selectCharacterById("");
    }

    @Override
    public Character searchCharacterById(String characterId) {
        if (StringUtil.isBlank(characterId)) {
            return null;
        }
        List<Character> characters = characterMapper.selectCharacterById(characterId);
        return characters.isEmpty() ? null : characters.get(0);
    }
}
