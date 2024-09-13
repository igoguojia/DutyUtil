package com.jnu.tool.authority.service.api;

import com.jnu.tool.authority.pojo.Authority;

import java.util.List;

/**
 * @author duya001
 */
public interface AuthorityService {
    /**
     * 增加一条Authority记录
     *
     * @param authority Authority对象
     * @return 添加成功标志位，成功返回true，失败返回false
     */
    boolean addAuthority(Authority authority);

    /**
     * 根据ID删除权限
     *
     * @param authorityId 权限ID
     * @return 删除成功标志位，成功返回true，失败返回false
     */
    boolean removeAuthorityById(String authorityId);

    /**
     * 根据ID更新Authority记录
     *
     * @param authority Authority对象
     * @return 更新成功标志位，成功返回true，失败返回false
     */
    boolean changeAuthority(Authority authority);

    /**
     * 查询全部权限
     *
     * @return 权限对象列表
     */
    List<Authority> searchAllAuthoritys();

    /**
     * 根据ID查询权限，当查询到多条记录时，返回第一条记录
     *
     * @param authorityId 权限ID
     * @return 权限对象列表
     */
    Authority searchAuthorityById(String authorityId);
}
