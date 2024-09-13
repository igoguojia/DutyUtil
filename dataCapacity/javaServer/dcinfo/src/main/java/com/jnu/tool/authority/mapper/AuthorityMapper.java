package com.jnu.tool.authority.mapper;

import com.jnu.tool.authority.pojo.Authority;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository("authorityMapper")
public interface AuthorityMapper {
    /**
     * 增加一条Authority记录
     *
     * @param authority Authority对象
     * @return 影响的记录数
     */
    int insertAuthority(Authority authority);

    /**
     * 根据ID删除权限
     *
     * @param authorityId 权限ID
     * @return 影响的记录数
     */
    int deleteAuthorityById(@Param("authorityId") String authorityId);

    /**
     * 根据ID更新Authority记录
     *
     * @param authority Authority对象
     * @return 影响的记录数
     */
    int updateAuthority(Authority authority);

    /**
     * 根据ID查询权限
     *
     * @param authorityId 权限ID
     * @return 权限对象列表
     */
    List<Authority> selectAuthorityById(@Param("authorityId") String authorityId);
}
