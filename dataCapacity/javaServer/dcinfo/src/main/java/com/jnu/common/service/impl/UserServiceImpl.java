package com.jnu.common.service.impl;

import com.iceolive.util.StringUtil;
import com.jnu.common.mapper.UserMapper;
import com.jnu.common.pojo.UserNN;
import com.jnu.common.service.api.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userService")
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;

    public UserServiceImpl(@Qualifier("userMapper") UserMapper userMapper) {
        this.userMapper = userMapper;
    }


    @Override
    public List<UserNN> searchUserNNByGroupName(String groupName) {
        if (StringUtil.isBlank(groupName)) {
            return null;
        }
        return userMapper.selectUserNNByGroupName(groupName);
    }
}
