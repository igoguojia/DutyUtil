package com.jnu.tool.authority.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iceolive.util.StringUtil;
import com.jnu.tool.authority.pojo.Authority;
import com.jnu.tool.authority.pojo.UserAuthority;
import com.jnu.tool.authority.service.api.UserAuthorityService;
import com.jnu.tool.utils.ResultEntity;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author duya001
 */
@CrossOrigin
@RestController
@RequestMapping("/userAuthority")
public class UserAuthorityController {
    private final UserAuthorityService userAuthorityService;

    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public UserAuthorityController(@Qualifier("userAuthorityService") UserAuthorityService userAuthorityService) {
        this.userAuthorityService = userAuthorityService;
    }

    /**
     * 不传入权限ID时，查询用户拥有的所有权限（普通权限 + 特殊权限）
     * 传入权限ID时，查询用户是否拥有该权限
     */
    @PostMapping("/retrieveAuthority")
    public String retrieveAuthorityInfoByUserId(@RequestParam(value = "userID", defaultValue = "") String userID,
                                                @RequestParam(value = "authorityId", defaultValue = "") String authorityId) throws JsonProcessingException {
        authorityId = authorityId.trim();
        ResultEntity<Object> resultEntity;
        // 对userID处理
        if (!StringUtil.isBlank(userID)) {
            userID = userID.trim();
            try {
                userID = Integer.parseInt(userID) + "";
            } catch (NumberFormatException e) {
                return ResultEntity.failWithoutData("用户ID异常！").returnResult();
            }
        }
        if (StringUtil.isBlank(authorityId)) {
            // 查询用户拥有的所有权限
            List<Authority> authorities = userAuthorityService.searchAllAuthorityByUserId(userID);
            resultEntity = ResultEntity.successWithData(authorities);
        } else {
            // 查询用户是否拥有该权限
            boolean owned = userAuthorityService.ownAuthority(userID, authorityId);
            if (owned) {
                resultEntity = ResultEntity.successWithoutData();
            } else {
                resultEntity = ResultEntity.failWithoutData("该用户没有该权限！");
            }
        }
        return OBJECT_MAPPER.writeValueAsString(resultEntity);
    }

    @PostMapping("/createUserAuthority")
    public String createUserAuthority(@RequestParam(value = "userID", defaultValue = "") String userID,
                                      @RequestParam(value = "authorityId", defaultValue = "") String authorityId) throws JsonProcessingException {
        if (!StringUtil.isBlank(userID)) {
            userID = userID.trim();
            try {
                userID = Integer.parseInt(userID) + "";
            } catch (NumberFormatException e) {
                return ResultEntity.failWithoutData("用户ID异常！").returnResult();
            }
        }
        authorityId = authorityId.trim();
        ResultEntity<Object> resultEntity;
        if (!userAuthorityService.existUserAuthority(userID, authorityId)) {
            UserAuthority userAuthority = new UserAuthority(userID, authorityId);
            boolean success = userAuthorityService.addUserAuthority(userAuthority);
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

    @PostMapping("/deleteUserAuthority")
    public String deleteUserAuthority(@RequestParam(value = "userID", defaultValue = "") String userID,
                                      @RequestParam(value = "authorityId", defaultValue = "") String authorityId) throws JsonProcessingException {
        if (!StringUtil.isBlank(userID)) {
            userID = userID.trim();
            try {
                userID = Integer.parseInt(userID) + "";
            } catch (NumberFormatException e) {
                return ResultEntity.failWithoutData("用户ID异常！").returnResult();
            }
        }
        authorityId = authorityId.trim();
        ResultEntity<Object> resultEntity;
        if (!userAuthorityService.existUserAuthority(userID, authorityId)) {
            resultEntity = ResultEntity.failWithoutData("数据库中无该记录！");
        } else {
            boolean success = userAuthorityService.removeUserAuthority(userID, authorityId);
            if (success) {
                resultEntity = ResultEntity.successWithoutData();
            } else {
                resultEntity = ResultEntity.failWithoutData("删除失败！");
            }
        }
        return OBJECT_MAPPER.writeValueAsString(resultEntity);
    }

    @PostMapping("/retrieveAllUserAuthority")
    public String retrieveAllUserAuthority() throws JsonProcessingException {
        List<UserAuthority> list = userAuthorityService.searchAllUserAuthority();
        ResultEntity<List<UserAuthority>> resultEntity;
        if (list == null || list.isEmpty()) {
            resultEntity = ResultEntity.failWithoutData("数据库中没有相关数据！");
        } else {
            resultEntity = ResultEntity.successWithData(list);
        }
        return OBJECT_MAPPER.writeValueAsString(resultEntity);
    }

    @PostMapping("/retrieveUserAuthorityByUserId")
    public String retrieveUserAuthorityByUserId(@RequestParam(value = "userID", defaultValue = "") String userID) throws JsonProcessingException {
        if (!StringUtil.isBlank(userID)) {
            userID = userID.trim();
            try {
                userID = Integer.parseInt(userID) + "";
            } catch (NumberFormatException e) {
                return ResultEntity.failWithoutData("用户ID异常！").returnResult();
            }
        }
        List<UserAuthority> list = userAuthorityService.searchUserAuthorityByUserId(userID);
        ResultEntity<List<UserAuthority>> resultEntity;
        if (list == null || list.isEmpty()) {
            resultEntity = ResultEntity.failWithoutData("数据库中没有相关数据！");
        } else {
            resultEntity = ResultEntity.successWithData(list);
        }
        return OBJECT_MAPPER.writeValueAsString(resultEntity);
    }

    @PostMapping("/retrieveUserAuthorityByAuthorityId")
    public String retrieveUserAuthorityByAuthorityId(@RequestParam(value = "authorityId", defaultValue = "") String authorityId) throws JsonProcessingException {
        authorityId = authorityId.trim();
        List<UserAuthority> list = userAuthorityService.searchUserAuthorityByAuthorityId(authorityId);
        ResultEntity<List<UserAuthority>> resultEntity;
        if (list == null || list.isEmpty()) {
            resultEntity = ResultEntity.failWithoutData("数据库中没有相关数据！");
        } else {
            resultEntity = ResultEntity.successWithData(list);
        }
        return OBJECT_MAPPER.writeValueAsString(resultEntity);
    }
}
