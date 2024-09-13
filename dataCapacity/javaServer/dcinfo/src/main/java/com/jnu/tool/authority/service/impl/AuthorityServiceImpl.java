package com.jnu.tool.authority.service.impl;

import com.iceolive.util.StringUtil;
import com.jnu.tool.authority.mapper.AuthorityMapper;
import com.jnu.tool.authority.pojo.Authority;
import com.jnu.tool.authority.service.api.AuthorityService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author duya001
 */
@Service("authorityService")
public class AuthorityServiceImpl implements AuthorityService {
    private final AuthorityMapper authorityMapper;

    public AuthorityServiceImpl(@Qualifier("authorityMapper") AuthorityMapper authorityMapper) {
        this.authorityMapper = authorityMapper;
    }

    @Override
    public boolean addAuthority(Authority authority) {
        if (StringUtil.isBlank(authority.getAuthorityId()) || StringUtil.isBlank(authority.getAuthorityDescription())) {
            return false;
        }
        return authorityMapper.insertAuthority(authority) != 0;
    }

    @Override
    public boolean removeAuthorityById(String authorityId) {
        if (StringUtil.isBlank(authorityId)) {
            return false;
        }
        return authorityMapper.deleteAuthorityById(authorityId) != 0;
    }

    @Override
    public boolean changeAuthority(Authority authority) {
        if (StringUtil.isBlank(authority.getAuthorityId()) || StringUtil.isBlank(authority.getAuthorityDescription())) {
            return false;
        }
        return authorityMapper.updateAuthority(authority) != 0;
    }

    @Override
    public List<Authority> searchAllAuthoritys() {
        return authorityMapper.selectAuthorityById("");
    }

    @Override
    public Authority searchAuthorityById(String authorityId) {
        if (StringUtil.isBlank(authorityId)) {
            return null;
        }
        List<Authority> authoritys = authorityMapper.selectAuthorityById(authorityId);
        return authoritys.isEmpty() ? null : authoritys.get(0);
    }
}
