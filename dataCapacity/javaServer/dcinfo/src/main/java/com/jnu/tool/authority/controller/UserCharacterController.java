package com.jnu.tool.authority.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iceolive.util.StringUtil;
import com.jnu.tool.authority.pojo.UserCharacter;
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
@RequestMapping("/userCharacter")
public class UserCharacterController {
    private final UserCharacterService userCharacterService;

    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public UserCharacterController(@Qualifier("userCharacterService") UserCharacterService userCharacterService) {
        this.userCharacterService = userCharacterService;
    }

    @PostMapping("/createUserCharacter")
    public String createUserCharacter(@RequestParam(value = "userID", defaultValue = "") String userID,
                                      @RequestParam(value = "characterId", defaultValue = "") String characterId) throws JsonProcessingException {
        if (!StringUtil.isBlank(userID)) {
            userID = userID.trim();
            try {
                userID = Integer.parseInt(userID) + "";
            } catch (NumberFormatException e) {
                return ResultEntity.failWithoutData("用户ID异常！").returnResult();
            }
        }
        characterId = characterId.trim();
        ResultEntity<Object> resultEntity;
        if (!userCharacterService.existUserCharacter(userID, characterId)) {
            UserCharacter userCharacter = new UserCharacter(userID, characterId);
            boolean success = userCharacterService.addUserCharacter(userCharacter);
            if (success) {
                resultEntity = ResultEntity.successWithoutData();
            } else {
                resultEntity = ResultEntity.failWithoutData("添加失败！");
            }
        } else {
            resultEntity = ResultEntity.failWithoutData("数据库中已有该记录！");
        }
        return OBJECT_MAPPER.writeValueAsString(resultEntity);
    }

    @PostMapping("/deleteUserCharacter")
    public String deleteUserCharacter(@RequestParam(value = "userID", defaultValue = "") String userID,
                                      @RequestParam(value = "characterId", defaultValue = "") String characterId) throws JsonProcessingException {
        if (!StringUtil.isBlank(userID)) {
            userID = userID.trim();
            try {
                userID = Integer.parseInt(userID) + "";
            } catch (NumberFormatException e) {
                return ResultEntity.failWithoutData("用户ID异常！").returnResult();
            }
        }
        characterId = characterId.trim();
        ResultEntity<Object> resultEntity;
        if (!userCharacterService.existUserCharacter(userID, characterId)) {
            resultEntity = ResultEntity.failWithoutData("数据库中无该记录！");
        } else {
            boolean success = userCharacterService.deleteUserCharacter(userID, characterId);
            if (success) {
                resultEntity = ResultEntity.successWithoutData();
            } else {
                resultEntity = ResultEntity.failWithoutData("删除失败！");
            }
        }
        return OBJECT_MAPPER.writeValueAsString(resultEntity);
    }

    @PostMapping("/retrieveAllUserCharacter")
    public String retrieveAllUserCharacter() throws JsonProcessingException {
        List<UserCharacter> list = userCharacterService.searchAllUserCharacter();
        ResultEntity<List<UserCharacter>> resultEntity;
        if (list == null || list.isEmpty()) {
            resultEntity = ResultEntity.failWithoutData("数据库中没有相关数据！");
        } else {
            resultEntity = ResultEntity.successWithData(list);
        }
        return OBJECT_MAPPER.writeValueAsString(resultEntity);
    }

    @PostMapping("/retrieveUserCharacterByUserId")
    public String retrieveUserControllerByUserId(@RequestParam(value = "userID", defaultValue = "") String userID) throws JsonProcessingException {
        if (!StringUtil.isBlank(userID)) {
            userID = userID.trim();
            try {
                userID = Integer.parseInt(userID) + "";
            } catch (NumberFormatException e) {
                return ResultEntity.failWithoutData("用户ID异常！").returnResult();
            }
        }
        List<UserCharacter> list = userCharacterService.searchUserCharacterByUserId(userID);
        ResultEntity<List<UserCharacter>> resultEntity;
        if (list == null || list.isEmpty()) {
            resultEntity = ResultEntity.failWithoutData("数据库中没有相关数据！");
        } else {
            resultEntity = ResultEntity.successWithData(list);
        }
        return OBJECT_MAPPER.writeValueAsString(resultEntity);
    }

    @PostMapping("/retrieveUserCharacterByCharacterId")
    public String retrieveUserControllerByCharacterId(@RequestParam(value = "characterId", defaultValue = "") String characterId) throws JsonProcessingException {
        characterId = characterId.trim();
        List<UserCharacter> list = userCharacterService.searchUserCharacterByCharacterId(characterId);
        ResultEntity<List<UserCharacter>> resultEntity;
        if (list == null || list.isEmpty()) {
            resultEntity = ResultEntity.failWithoutData("数据库中没有相关数据！");
        } else {
            resultEntity = ResultEntity.successWithData(list);
        }
        return OBJECT_MAPPER.writeValueAsString(resultEntity);
    }
}
