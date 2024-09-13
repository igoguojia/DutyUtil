package com.jnu.tool.authority.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jnu.tool.authority.pojo.CharacterAuthority;
import com.jnu.tool.authority.service.api.CharacterAuthorityService;
import com.jnu.tool.utils.ResultEntity;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author duya001
 */
@CrossOrigin
@RestController
@RequestMapping("/characterAuthority")
public class CharacterAuthorityController {
    private final CharacterAuthorityService characterAuthorityService;

    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public CharacterAuthorityController(@Qualifier("characterAuthorityService") CharacterAuthorityService characterAuthorityService) {
        this.characterAuthorityService = characterAuthorityService;
    }

    @PostMapping("/createCharacterAuthority")
    public String createCharacterAuthority(@RequestParam(value = "characterId", defaultValue = "") String characterId,
                                           @RequestParam(value = "authorityId", defaultValue = "") String authorityId) throws JsonProcessingException {
        characterId = (characterId == null ? null : characterId.trim());
        authorityId = (authorityId == null ? null : authorityId.trim());
        ResultEntity<Object> resultEntity;
        if (!characterAuthorityService.existCharacterAuthority(characterId, authorityId)) {
            // 如果不存在才允许创建
            CharacterAuthority characterAuthority = new CharacterAuthority(characterId, authorityId);
            boolean success = characterAuthorityService.addCharacterAuthority(characterAuthority);
            if (success) {
                resultEntity = ResultEntity.successWithoutData();
            } else {
                resultEntity = ResultEntity.failWithoutData("添加失败！");
            }
        } else {
            // 如果存在则不允许创建
            resultEntity = ResultEntity.failWithoutData("数据库中已有该记录！");
        }
        return OBJECT_MAPPER.writeValueAsString(resultEntity);
    }

    @PostMapping("/deleteCharacterAuthority")
    public String deleteCharacterAuthority(@RequestParam(value = "characterId", defaultValue = "") String characterId,
                                           @RequestParam(value = "authorityId", defaultValue = "") String authorityId) throws JsonProcessingException {
        characterId = characterId.trim();
        authorityId = authorityId.trim();
        ResultEntity<Object> resultEntity;
        if (!characterAuthorityService.existCharacterAuthority(characterId, authorityId)) {
            resultEntity = ResultEntity.failWithoutData("数据库中无该记录！");
        } else {
            boolean success = characterAuthorityService.removeCharacterAuthority(characterId, authorityId);
            if (success) {
                resultEntity = ResultEntity.successWithoutData();
            } else {
                resultEntity = ResultEntity.failWithoutData("删除失败！");
            }
        }
        return OBJECT_MAPPER.writeValueAsString(resultEntity);
    }

    @PostMapping("/retrieveAllCharacterAuthority")
    public String retrieveAllCharacterAuthority() throws JsonProcessingException {
        List<CharacterAuthority> list = characterAuthorityService.searchAllCharacterAuthority();
        ResultEntity<List<CharacterAuthority>> resultEntity;
        if (list == null || list.isEmpty()) {
            resultEntity = ResultEntity.failWithoutData("数据库中没有相关数据！");
        } else {
            resultEntity = ResultEntity.successWithData(list);
        }
        return OBJECT_MAPPER.writeValueAsString(resultEntity);
    }

    @PostMapping("/retrieveCharacterAuthorityByCharacterId")
    public String retrieveCharacterAuthorityByCharacterId(@RequestParam(value = "characterId", defaultValue = "") String characterId) throws JsonProcessingException {
        characterId = characterId.trim();
        List<CharacterAuthority> list = characterAuthorityService.searchCharacterAuthorityByCharacterId(characterId);
        ResultEntity<List<CharacterAuthority>> resultEntity;
        if (list == null || list.isEmpty()) {
            resultEntity = ResultEntity.failWithoutData("数据库中没有相关数据！");
        } else {
            resultEntity = ResultEntity.successWithData(list);
        }
        return OBJECT_MAPPER.writeValueAsString(resultEntity);
    }

    @PostMapping("/retrieveCharacterAuthorityByAuthorityId")
    public String retrieveCharacterAuthorityByAuthorityId(@RequestParam(value = "authorityId", defaultValue = "") String authorityId) throws JsonProcessingException {
        authorityId = authorityId.trim();
        List<CharacterAuthority> list = characterAuthorityService.searchCharacterAuthorityByAuthorityId(authorityId);
        ResultEntity<List<CharacterAuthority>> resultEntity;
        if (list == null || list.isEmpty()) {
            resultEntity = ResultEntity.failWithoutData("数据库中没有相关数据！");
        } else {
            resultEntity = ResultEntity.successWithData(list);
        }
        return OBJECT_MAPPER.writeValueAsString(resultEntity);
    }
}
