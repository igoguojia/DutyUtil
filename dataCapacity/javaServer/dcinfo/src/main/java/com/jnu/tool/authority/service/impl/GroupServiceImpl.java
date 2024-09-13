package com.jnu.tool.authority.service.impl;

import com.iceolive.util.StringUtil;
import com.jnu.tool.authority.mapper.GroupMapper;
import com.jnu.tool.authority.pojo.Group;
import com.jnu.tool.authority.service.api.GroupService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("groupService")
public class GroupServiceImpl implements GroupService {
    private final GroupMapper groupMapper;

    public GroupServiceImpl(@Qualifier("groupMapper") GroupMapper groupMapper) {
        this.groupMapper = groupMapper;
    }

    @Override
    public boolean addGroup(Group group) {
        if (StringUtil.isBlank(group.getGroupName()) || StringUtil.isBlank(group.getGroupDescription())) {
            return false;
        }
        return groupMapper.insertGroup(group) != 0;
    }

    @Override
    public boolean removeGroupByName(String groupName) {
        if (StringUtil.isBlank(groupName)) {
            return false;
        }
        return groupMapper.deleteGroupByName(groupName) != 0;
    }

    @Override
    public boolean changeGroup(Group group) {
        if (StringUtil.isBlank(group.getGroupName()) || StringUtil.isBlank(group.getGroupDescription())) {
            return false;
        }
        return groupMapper.updateGroup(group) != 0;
    }

    @Override
    public List<Group> searchAllGroups() {
        return groupMapper.selectGroupByName("");
    }

    @Override
    public Group searchGroupByName(String groupName) {
        if (StringUtil.isBlank(groupName)) {
            return null;
        }
        List<Group> groups = groupMapper.selectGroupByName(groupName);
        return groups.isEmpty() ? null : groups.get(0);
    }
}
