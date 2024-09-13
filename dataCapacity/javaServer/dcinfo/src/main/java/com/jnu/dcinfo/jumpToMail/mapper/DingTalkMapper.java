package com.jnu.dcinfo.jumpToMail.mapper;

import com.jnu.dcinfo.jumpToMail.pojo.DingTalk;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author duya001
 * 
 */
@Mapper
@Repository("DingTalkMapper")
public interface DingTalkMapper {

    /**
     * 查找所有的钉钉机器人
     * @return 查找到的机器人信息
     */
    List<DingTalk> selectAllDT();

    /**
     * 根据群组名查找机器人
     * @param groupName
     * @return 查找到的机器人信息
     */
    DingTalk selectByGroupName(@Param("groupName") String groupName);

    /**
     * 新增一条钉钉机器人记录
     * @param dingTalk
     * @return 影响的记录数
     */
    int insertDingTalk(DingTalk dingTalk);

    /**
     * 根据群组名删除一条钉钉机器人记录
     * @param groupName
     * @return 影响的记录数
     */
    int deleteDingTalk(@Param("groupName") String groupName);

    /**
     * 根据群组名更新一条钉钉机器人记录
     * @param dingTalk
     * @return 影响的记录数
     */
    int updateDingTalk(DingTalk dingTalk);

}
