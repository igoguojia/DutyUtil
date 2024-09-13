package com.jnu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jnu.common.pojo.User;
import com.jnu.common.pojo.UserNN;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 */
@Mapper
@Repository("userMapper")
public interface UserMapper extends BaseMapper<User> {
    /**
     * 根据工号查询用户
     *
     * @param operatorNo 工号
     * @return
     */
    @Select("select * from DCUSERS where OPERATOR_NO = #{operatorNo}")
    User selectByOperatorNo(@Param("operatorNo") String operatorNo);

    @Select("select OPERATOR_NO,OPERATOR_NAME from DCUSERS where GROUP_NAME = #{groupName}")
    List<UserNN> selectUserNNByGroupName(@Param("groupName") String groupName);

    @Select("select B.OPERATOR_NO from DCUSERS A, DCUSERS B WHERE A.OPERATOR_NO = #{operatorNo} AND A.GROUP_NAME = B.GROUP_NAME")
    List<String> selectGroupListByOperatorNo(@Param("operatorNo") String operatorNo);

    @Select("select OPERATOR_NO from DCUSERS where GROUP_NAME = #{groupName} and IS_LEADER = '1'")
    List<String> selectLeaderNoListByGroupName(@Param("groupName") String groupName);

    List<String> selectEmailByOperatorNo(@Param("operatorNoList") List<String> operatorNoList);

    List<String> selectPhoneNumberByOperatorNo(@Param("operatorNoList") List<String> operatorNoList);
}
