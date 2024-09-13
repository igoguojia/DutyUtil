package com.jnu.tool.authority.service.impl;

import com.iceolive.util.StringUtil;
import com.jnu.tool.authority.mapper.UserAuthorityMapper;
import com.jnu.tool.authority.pojo.Authority;
import com.jnu.tool.authority.pojo.UserAuthority;
import com.jnu.tool.authority.service.api.UserAuthorityService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author duya001
 */
@Service("userAuthorityService")
public class UserAuthorityServiceImpl implements UserAuthorityService {
    private final UserAuthorityMapper userAuthorityMapper;

    public UserAuthorityServiceImpl(@Qualifier("userAuthorityMapper") UserAuthorityMapper userAuthorityMapper) {
        this.userAuthorityMapper = userAuthorityMapper;
    }

    @Override
    public boolean addUserAuthority(UserAuthority userAuthority) {
        if (StringUtil.isBlank(userAuthority.getAuthorityId()) || StringUtil.isBlank(userAuthority.getUserID())) {
            return false;
        }
        return userAuthorityMapper.insertUserAuthority(userAuthority) != 0;
    }

    @Override
    public boolean removeUserAuthority(String userId, String authorityId) {
        if (StringUtil.isBlank(userId) || StringUtil.isBlank(authorityId)) {
            return false;
        }
        return userAuthorityMapper.deleteUserAuthority(userId, authorityId) != 0;
    }

    @Override
    public List<UserAuthority> searchAllUserAuthority() {
        return userAuthorityMapper.selectAllUserAuthority();
    }

    @Override
    public List<UserAuthority> searchUserAuthorityByUserId(String userId) {
        if (StringUtil.isBlank(userId)) {
            return null;
        }
        return userAuthorityMapper.selectUserAuthorityByUserId(userId);
    }

    @Override
    public List<UserAuthority> searchUserAuthorityByAuthorityId(String authorityId) {
        if (StringUtil.isBlank(authorityId)) {
            return null;
        }
        return userAuthorityMapper.selectUserAuthorityByAuthorityId(authorityId);
    }

    @Override
    public boolean existUserAuthority(String userId, String authorityId) {
        if (StringUtil.isBlank(userId) || StringUtil.isBlank(authorityId)) {
            return false;
        }
        List<UserAuthority> list = searchUserAuthorityByUserId(userId);
        List<UserAuthority> collect = list.stream().filter((element) -> authorityId.equals(element.getAuthorityId())).collect(Collectors.toList());
        return !collect.isEmpty();
    }

    @Override
    public List<Authority> searchAllAuthorityByUserId(String userId) {
        if (StringUtil.isBlank(userId)) {
            return null;
        }
        return userAuthorityMapper.selectAllAuthorityByUserId(userId);
    }

    @Override
    public boolean ownAuthority(String userId, String authorityId) {
        if (StringUtil.isBlank(authorityId)) {
            return false;
        }
        List<Authority> list = userAuthorityMapper.selectAllAuthorityByUserId(userId);
        List<Authority> collect = list.stream().filter((element) -> element.getAuthorityId().equals(authorityId)).collect(Collectors.toList());
        return !collect.isEmpty();
    }
}
