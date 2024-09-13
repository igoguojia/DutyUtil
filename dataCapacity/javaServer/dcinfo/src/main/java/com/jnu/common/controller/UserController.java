package com.jnu.common.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jnu.common.pojo.UserNN;
import com.jnu.common.service.api.UserService;
import com.jnu.tool.utils.ResultEntity;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(@Qualifier("userService") UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/getUserNNBygroupName")
    public String getUserNNByGroupName(@RequestParam(value = "groupName", defaultValue = "") String groupName) throws JsonProcessingException {
        List<UserNN> list = userService.searchUserNNByGroupName(groupName);
        if (list == null || list.isEmpty()) {
            return ResultEntity.failWithoutData("数据库中没有相关数据！").returnResult();
        } else {
            return ResultEntity.successWithData(list).returnResult();
        }
    }
}
