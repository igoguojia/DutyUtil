package com.jnu.tool.authority.service.impl;

import com.iceolive.util.StringUtil;
import com.jnu.tool.authority.mapper.UserCharacterMapper;
import com.jnu.tool.authority.pojo.UserCharacter;
import com.jnu.tool.authority.service.api.UserCharacterService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author duya001
 */
@Scope("prototype")
@Service("userCharacterService")
public class UserCharacterServiceImpl implements UserCharacterService {
    private final UserCharacterMapper userCharacterMapper;

    public UserCharacterServiceImpl(@Qualifier("userCharacterMapper") UserCharacterMapper userCharacterMapper) {
        this.userCharacterMapper = userCharacterMapper;
    }

    @Override
    public boolean addUserCharacter(UserCharacter userCharacter) {
        if (StringUtil.isBlank(userCharacter.getCharacterId()) || StringUtil.isBlank(userCharacter.getUserID())) {
            return false;
        }
        return userCharacterMapper.insertUserCharacter(userCharacter) != 0;
    }

    @Override
    public boolean deleteUserCharacter(String userId, String characterId) {
        if (StringUtil.isBlank(userId) || StringUtil.isBlank(characterId)) {
            return false;
        }
        return userCharacterMapper.deleteUserCharacter(userId, characterId) != 0;
    }

    @Override
    public List<UserCharacter> searchAllUserCharacter() {
        return userCharacterMapper.selectAllUserCharacter();
    }

    @Override
    public List<UserCharacter> searchUserCharacterByUserId(String userId) {
        if (StringUtil.isBlank(userId)) {
            return null;
        }
        return userCharacterMapper.selectUserCharacterByUserId(userId);
    }

    @Override
    public List<UserCharacter> searchUserCharacterByCharacterId(String characterId) {
        if (StringUtil.isBlank(characterId)) {
            return null;
        }
        return userCharacterMapper.selectUserCharacterByCharacterId(characterId);
    }

    @Override
    public boolean existUserCharacter(String userId, String characterId) {
        if (StringUtil.isBlank(userId) || StringUtil.isBlank(characterId)) {
            return false;
        }
        List<UserCharacter> list = searchUserCharacterByUserId(userId);
        List<UserCharacter> collect = list.stream().filter((element) -> characterId.equals(element.getCharacterId())).collect(Collectors.toList());
        return !collect.isEmpty();
    }
}
