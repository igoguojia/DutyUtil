package com.jnu.dcinfo.jumpToMail.service;

import com.jnu.dcinfo.jumpToMail.pojo.DingTalk;

import java.util.List;

/**
 * @author duya001
 *
 */
public interface DingTalkService {

    String insertDingTalk(DingTalk dingTalk);

    String deleteDingTalk(String groupName);

    List<DingTalk> selectAllDT();

    DingTalk selectByGroupName(String groupName);

    String updateDingTalk(DingTalk dingTalk);
}
