package com.jnu.dcinfo.jumpToMail.controller;

import com.jnu.dcinfo.jumpToMail.pojo.DingTalk;
import com.jnu.dcinfo.jumpToMail.service.DingTalkService;
import com.jnu.dcinfo.jumpToMail.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author duya001
 * 
 */
@CrossOrigin
@RestController
@RequestMapping("/dingTalk")
public class DingTalkController {

    @Autowired
    private DingTalkService dingTalkService;

    /**
     * 新增钉钉机器人信息
     * @param webHook
     * @param groupName
     * @param secret
     * @return
     */
    @RequestMapping("/insertDingTalk")
    public Result insertDingTalk(@RequestParam("webHook") String webHook,
                                 @RequestParam("groupName") String groupName,
                                 @RequestParam("secret") String secret) {


        DingTalk dingTalk = new DingTalk(webHook, groupName, secret);
        String result = dingTalkService.insertDingTalk(dingTalk);
        if ("新增成功".equals(result)){
            return new Result(true, result, null);
        } else {
            return new Result(false, result, null);
        }
    }

    /**
     * 删除对应群组名的钉钉机器人记录
     * @param groupName
     * @return
     */
    @RequestMapping("/deleteDingTalk")
    public Result deleteDingTalk(@RequestParam("groupName") String groupName){

        String result = dingTalkService.deleteDingTalk(groupName);
        if ("删除成功".equals(result)){
            return new Result(true, result, null);
        } else {
            return new Result(false, result, null);
        }
    }

    /**
     * 更新对应群组名的钉钉机器人信息
     * @param webHook
     * @param groupName
     * @param secret
     * @return
     */
    @RequestMapping("/updateDingTalk")
    public Result updateDingTalk(@RequestParam("webHook") String webHook,
                                 @RequestParam("groupName") String groupName,
                                 @RequestParam("secret") String secret){

        DingTalk dingTalk = new DingTalk(webHook, groupName, secret);
        String result = dingTalkService.updateDingTalk(dingTalk);

        if ("更新成功".equals(result)){
            return new Result(true, result, null);
        } else {
            return new Result(false, result, null);
        }
    }

    /**
     * 查找所有机器人信息
     * @return
     */
    @RequestMapping("/selectAllDT")
    public Result selectAllDT(){
        return new Result(true, "查找完成", dingTalkService.selectAllDT());
    }

    /**
     * 查找对应群组名机器人信息
     * @param groupName
     * @return
     */
    @RequestMapping("/selectByGroupName")
    public Result selectByGroupName(@RequestParam("groupName") String groupName){
        if (groupName.length() > 0){
            DingTalk dingTalk = dingTalkService.selectByGroupName(groupName);
            if (dingTalk == null){
                return new Result(true, "查找完成,未查找到相应记录", null);

            } else {
                return new Result(true, "查找完成", dingTalk);
            }
        } else {
            return new Result(false, "groupName为空", null);
        }
    }

}
