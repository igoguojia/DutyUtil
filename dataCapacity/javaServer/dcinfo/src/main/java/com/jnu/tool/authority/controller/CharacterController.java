package com.jnu.tool.authority.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jnu.tool.authority.pojo.Character;
import com.jnu.tool.authority.pojo.UserCharacter;
import com.jnu.tool.authority.service.api.CharacterService;
import com.jnu.tool.authority.service.api.UserCharacterService;
import com.jnu.tool.utils.ResultEntity;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author duya001
 */
@CrossOrigin
@RestController
@RequestMapping("/character")
public class CharacterController {
    private final CharacterService characterService;

    private final UserCharacterService userCharacterService;

    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public CharacterController(@Qualifier("characterService") CharacterService characterService, @Qualifier("userCharacterService") UserCharacterService userCharacterService) {
        this.characterService = characterService;
        this.userCharacterService = userCharacterService;
    }

    @PostMapping("/createCharacter")
    public String createCharacter(@RequestParam(value = "characterId", defaultValue = "") String characterId,
                                  @RequestParam(value = "characterDescription", defaultValue = "") String characterDescription) throws JsonProcessingException {
        characterId = characterId.trim();
        characterDescription = characterDescription.trim();
        Character existedCharacter = characterService.searchCharacterById(characterId);
        ResultEntity<Object> resultEntity;
        if (existedCharacter == null) {
            Character character = new Character(characterId, characterDescription);
            boolean success = characterService.addCharacter(character);
            if (success) {
                resultEntity = ResultEntity.successWithoutData();
            } else {
                resultEntity = ResultEntity.failWithoutData("添加角色失败！");
            }
        } else {
            resultEntity = ResultEntity.failWithoutData("数据库中已经有该角色！");
        }
        return OBJECT_MAPPER.writeValueAsString(resultEntity);
    }

    @PostMapping("/retrieveAllCharacter")
    public String retrieveAllCharacters() throws JsonProcessingException {
        List<Character> characters = characterService.searchAllCharacters();
        ResultEntity<List<Character>> resultEntity = ResultEntity.successWithData(characters);
        return OBJECT_MAPPER.writeValueAsString(resultEntity);
    }

    @PostMapping("/retrieveCharacterById")
    public String retrieveCharacterById(@RequestParam(value = "characterId", defaultValue = "") String characterId) throws JsonProcessingException {
        characterId = characterId.trim();
        Character character = characterService.searchCharacterById(characterId);
        ResultEntity<Object> resultEntity;
        if (character == null) {
            resultEntity = ResultEntity.failWithoutData("数据库中无该角色！");
        } else {
            resultEntity = ResultEntity.successWithData(character);
        }
        return OBJECT_MAPPER.writeValueAsString(resultEntity);
    }

    @PostMapping("/updateCharacter")
    public String updateCharacter(@RequestParam(value = "characterId", defaultValue = "") String characterId,
                                  @RequestParam(value = "characterDescription", defaultValue = "") String characterDescription) throws JsonProcessingException {
        characterId = characterId.trim();
        characterDescription = characterDescription.trim();
        Character existedCharacter = characterService.searchCharacterById(characterId);
        ResultEntity<Object> resultEntity;
        if (existedCharacter == null) {
            resultEntity = ResultEntity.failWithoutData("数据库中无该角色！");
        } else {
            Character character = new Character(characterId, characterDescription);
            boolean success = characterService.changeCharacter(character);
            if (success) {
                resultEntity = ResultEntity.successWithoutData();
            } else {
                resultEntity = ResultEntity.failWithoutData("修改角色失败！");
            }
        }
        return OBJECT_MAPPER.writeValueAsString(resultEntity);
    }

    @PostMapping("/deleteCharacterById")
    public String deleteCharacterById(@RequestParam(value = "characterId", defaultValue = "") String characterId) throws JsonProcessingException {
        characterId = characterId.trim();
        Character existedCharacter = characterService.searchCharacterById(characterId);
        List<UserCharacter> list = userCharacterService.searchUserCharacterByCharacterId(characterId);
        ResultEntity<Object> resultEntity;
        if (list != null && !list.isEmpty()) {
            resultEntity = ResultEntity.failWithoutData("用户角色表中有角色数据，因此不可删除！");
        } else {
            if (existedCharacter == null) {
                resultEntity = ResultEntity.failWithoutData("数据库中无该角色！");
            } else {
                boolean success = characterService.removeCharacterById(characterId);
                if (success) {
                    resultEntity = ResultEntity.successWithoutData();
                } else {
                    resultEntity = ResultEntity.failWithoutData("删除角色失败！");
                }
            }
        }
        return OBJECT_MAPPER.writeValueAsString(resultEntity);
    }
}
