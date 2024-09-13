package com.jnu.dcinfo.jumpToMail.service.serviceImpl;

import com.jnu.dcinfo.jumpToMail.mapper.DingTalkMapper;
import com.jnu.dcinfo.jumpToMail.pojo.DingTalk;
import com.jnu.dcinfo.jumpToMail.service.DingTalkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author duya001
 * 
 */
@Service
public class DingTalkServiceImpl implements DingTalkService {

    @Autowired
    DingTalkMapper dingTalkMapper;

    @Override
    public String insertDingTalk(DingTalk dingTalk) {

        if (dingTalk.getGroupName().length() > 0){
            if (dingTalkMapper.selectByGroupName(dingTalk.getGroupName()) == null){
                dingTalkMapper.insertDingTalk(dingTalk);
                return "新增成功";
            } else {
                return "当前数据库已有该群组名对应记录，请检查参数，重新添加";
            }
        } else {
            return "钉钉机器人所属群组名为空，请检查参数";
        }
    }

    @Override
    public String deleteDingTalk(String groupName) {

        if (groupName.length() > 0){
            if (dingTalkMapper.selectByGroupName(groupName) != null){
                dingTalkMapper.deleteDingTalk(groupName);
                return "删除成功";
            } else {
                return "当前数据库内无群组名为【" + groupName + "】的记录";
            }
        } else {
            return "groupName参数为空，请检查参数";
        }
    }

    @Override
    public List<DingTalk> selectAllDT() {
        return dingTalkMapper.selectAllDT();
    }

    @Override
    public DingTalk selectByGroupName(String groupName) {

        return dingTalkMapper.selectByGroupName(groupName);
    }

    @Override
    public String updateDingTalk(DingTalk dingTalk) {

        if (dingTalk.getGroupName().length() > 0){
            if (dingTalkMapper.selectByGroupName(dingTalk.getGroupName()) != null){
                dingTalkMapper.updateDingTalk(dingTalk);
                return "更新成功";
            } else {
                return "当前数据库未查到对应记录，请检查参数";
            }
        } else {
            return "钉钉机器人所属群组名为空，请检查参数";
        }
    }

}
