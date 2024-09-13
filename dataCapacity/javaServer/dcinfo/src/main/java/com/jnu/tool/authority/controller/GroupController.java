package com.jnu.tool.authority.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jnu.common.pojo.UserNN;
import com.jnu.common.service.api.UserService;
import com.jnu.tool.authority.pojo.Group;
import com.jnu.tool.authority.service.api.GroupService;
import com.jnu.tool.utils.ResultEntity;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author duya001
 */
@CrossOrigin
@RestController
@RequestMapping("/group")
public class GroupController {
    private final GroupService groupService;

    private final UserService userService;

    public GroupController(@Qualifier("groupService") GroupService groupService, @Qualifier("userService") UserService userService) {
        this.groupService = groupService;
        this.userService = userService;
    }

    @PostMapping("/createGroup")
    public String createGroup(@RequestParam(value = "groupName", defaultValue = "") String groupName,
                              @RequestParam(value = "groupDescription", defaultValue = "") String groupDescription) throws JsonProcessingException {
        groupName = groupName.trim();
        groupDescription = groupDescription.trim();
        Group existedGroup = groupService.searchGroupByName(groupName);
        ResultEntity<Object> resultEntity;
        if (existedGroup == null) {
            Group group = new Group(groupName, groupDescription);
            boolean success = groupService.addGroup(group);
            if (success) {
                resultEntity = ResultEntity.successWithoutData();
            } else {
                resultEntity = ResultEntity.failWithoutData("添加角色失败！");
            }
        } else {
            resultEntity = ResultEntity.failWithoutData("数据库中已经有该组别！");
        }
        return resultEntity.returnResult();
    }

    @PostMapping("/retrieveAllGroup")
    public String retrieveAllGroups() throws JsonProcessingException {
        List<Group> groups = groupService.searchAllGroups();
        ResultEntity<List<Group>> resultEntity = ResultEntity.successWithData(groups);
        return resultEntity.returnResult();
    }

    @PostMapping("/retrieveGroupByName")
    public String retrieveGroupByName(@RequestParam(value = "groupName", defaultValue = "") String groupName) throws JsonProcessingException {
        groupName = groupName.trim();
        Group group = groupService.searchGroupByName(groupName);
        ResultEntity<Object> resultEntity;
        if (group == null) {
            resultEntity = ResultEntity.failWithoutData("数据库中无该组别！");
        } else {
            resultEntity = ResultEntity.successWithData(group);
        }
        return resultEntity.returnResult();
    }

    @PostMapping("/updateGroup")
    public String updateGroup(@RequestParam(value = "groupName", defaultValue = "") String groupName,
                              @RequestParam(value = "groupDescription", defaultValue = "") String groupDescription) throws JsonProcessingException {
        groupName = groupName.trim();
        groupDescription = groupDescription.trim();
        Group existedGroup = groupService.searchGroupByName(groupName);
        ResultEntity<Object> resultEntity;
        if (existedGroup == null) {
            resultEntity = ResultEntity.failWithoutData("数据库中无该组别！");
        } else {
            Group group = new Group(groupName, groupDescription);
            boolean success = groupService.changeGroup(group);
            if (success) {
                resultEntity = ResultEntity.successWithoutData();
            } else {
                resultEntity = ResultEntity.failWithoutData("修改组别失败！");
            }
        }
        return resultEntity.returnResult();
    }

    @PostMapping("/deleteGroupByName")
    public String deleteGroupByName(@RequestParam(value = "groupName", defaultValue = "") String groupName) throws JsonProcessingException {
        groupName = groupName.trim();
        Group existedGroup = groupService.searchGroupByName(groupName);
        List<UserNN> list = userService.searchUserNNByGroupName(groupName);
        ResultEntity<Object> resultEntity;
        if (list != null && !list.isEmpty()) {
            resultEntity = ResultEntity.failWithoutData("用户表中有组别数据，因此不可删除！");
        } else {
            if (existedGroup == null) {
                resultEntity = ResultEntity.failWithoutData("数据库中无该组别！");
            } else {
                boolean success = groupService.removeGroupByName(groupName);
                if (success) {
                    resultEntity = ResultEntity.successWithoutData();
                } else {
                    resultEntity = ResultEntity.failWithoutData("删除组别失败！");
                }
            }
        }
        return resultEntity.returnResult();
    }
}
