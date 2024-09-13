package com.jnu.tool.authority.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jnu.tool.authority.pojo.Authority;
import com.jnu.tool.authority.pojo.CharacterAuthority;
import com.jnu.tool.authority.pojo.UserAuthority;
import com.jnu.tool.authority.service.api.AuthorityService;
import com.jnu.tool.authority.service.api.CharacterAuthorityService;
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
@RequestMapping("/authority")
public class AuthorityController {
    private final AuthorityService authorityService;

    private final UserAuthorityService userAuthorityService;
    private final CharacterAuthorityService characterAuthorityService;

    public AuthorityController(@Qualifier("authorityService") AuthorityService authorityService,
                               @Qualifier("userAuthorityService") UserAuthorityService userAuthorityService,
                               @Qualifier("characterAuthorityService") CharacterAuthorityService characterAuthorityService) {
        this.authorityService = authorityService;
        this.userAuthorityService = userAuthorityService;
        this.characterAuthorityService = characterAuthorityService;
    }

    @PostMapping("/createAuthority")
    public String createAuthority(@RequestParam(value = "authorityId", defaultValue = "") String authorityId,
                                  @RequestParam(value = "authorityDescription", defaultValue = "") String authorityDescription) throws JsonProcessingException {
        authorityId = authorityId.trim();
        authorityDescription = authorityDescription.trim();
        Authority existedAuthority = authorityService.searchAuthorityById(authorityId);
        ResultEntity<Object> resultEntity;
        if (existedAuthority == null) {
            Authority authority = new Authority(authorityId, authorityDescription);
            boolean success = authorityService.addAuthority(authority);
            if (success) {
                resultEntity = ResultEntity.successWithoutData();
            } else {
                resultEntity = ResultEntity.failWithoutData("添加权限失败！");
            }
        } else {
            resultEntity = ResultEntity.failWithoutData("数据库中已经有该权限！");
        }
        return resultEntity.returnResult();
    }

    @PostMapping("/retrieveAllAuthority")
    public String retrieveAllAuthoritys() throws JsonProcessingException {
        List<Authority> authoritys = authorityService.searchAllAuthoritys();
        ResultEntity<List<Authority>> resultEntity = ResultEntity.successWithData(authoritys);
        return resultEntity.returnResult();
    }

    @PostMapping("/retrieveAuthorityById")
    public String retrieveAuthorityById(@RequestParam(value = "authorityId", defaultValue = "") String authorityId) throws JsonProcessingException {
        authorityId = authorityId.trim();
        Authority authority = authorityService.searchAuthorityById(authorityId);
        ResultEntity<Object> resultEntity;
        if (authority == null) {
            resultEntity = ResultEntity.failWithoutData("数据库中无该权限！");
        } else {
            resultEntity = ResultEntity.successWithData(authority);
        }
        return resultEntity.returnResult();
    }

    @PostMapping("/updateAuthority")
    public String updateAuthority(@RequestParam(value = "authorityId", defaultValue = "") String authorityId,
                                  @RequestParam(value = "authorityDescription", defaultValue = "") String authorityDescription) throws JsonProcessingException {
        authorityId = authorityId.trim();
        authorityDescription = authorityDescription.trim();
        Authority existedAuthority = authorityService.searchAuthorityById(authorityId);
        ResultEntity<Object> resultEntity;
        if (existedAuthority == null) {
            resultEntity = ResultEntity.failWithoutData("数据库中无该权限！");
        } else {
            Authority authority = new Authority(authorityId, authorityDescription);
            boolean success = authorityService.changeAuthority(authority);
            if (success) {
                resultEntity = ResultEntity.successWithoutData();
            } else {
                resultEntity = ResultEntity.failWithoutData("修改权限失败！");
            }
        }
        return resultEntity.returnResult();
    }

    @PostMapping("/deleteAuthorityById")
    public String deleteAuthorityById(@RequestParam(value = "authorityId", defaultValue = "") String authorityId) throws JsonProcessingException {
        authorityId = authorityId.trim();
        Authority existedAuthority = authorityService.searchAuthorityById(authorityId);
        List<UserAuthority> list = userAuthorityService.searchUserAuthorityByAuthorityId(authorityId);
        ResultEntity<Object> resultEntity;
        if (list != null && !list.isEmpty()) {
            resultEntity = ResultEntity.failWithoutData("用户权限表中有权限数据，因此不可删除！");
        }
        else {
            List<CharacterAuthority> list2 = characterAuthorityService.searchCharacterAuthorityByAuthorityId(authorityId);
            if (list2 != null && !list2.isEmpty()) {
                resultEntity = ResultEntity.failWithoutData("角色权限表中有权限数据，因此不可删除！");
            }
            else{if (existedAuthority == null) {
                resultEntity = ResultEntity.failWithoutData("数据库中无该权限！");
            } else {
                boolean success = authorityService.removeAuthorityById(authorityId);
                if (success) {
                    resultEntity = ResultEntity.successWithoutData();
                } else {
                    resultEntity = ResultEntity.failWithoutData("删除权限失败！");
                }
            }}

        }
        return resultEntity.returnResult();
    }
}
