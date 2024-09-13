package com.jnu.dcinfo.jumpToMail.controller;

import com.alibaba.fastjson.JSONObject;
import com.jnu.dcinfo.jumpToMail.pojo.DingTalk;
import com.jnu.dcinfo.jumpToMail.pojo.SendRequestParam;
import com.jnu.dcinfo.jumpToMail.service.DingTalkService;
import com.jnu.dcinfo.jumpToMail.service.SendDingService;
import com.jnu.dcinfo.jumpToMail.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@CrossOrigin
@RestController
@RequestMapping("/dingSend")
public class SendDingController {
    private final DingTalkService dingTalkService;

    private final SendDingService sendDingService;

    @Autowired
    public SendDingController(SendDingService sendDingService,DingTalkService dingTalkService){
        this.sendDingService = sendDingService;
        this.dingTalkService = dingTalkService;
    }

    @RequestMapping("/sendDingTalk")
    public Result sendDingTalk(@RequestBody JSONObject jsonObject) {
        String groupName = jsonObject.getObject("groupName", String.class);
        String msgType = jsonObject.getObject("msgType", String.class);
        SendRequestParam.At at = jsonObject.getObject("at", SendRequestParam.At.class);
        DingTalk dingTalk = dingTalkService.selectByGroupName(groupName);
        sendDingService.setSecret(dingTalk.getSecret());
        sendDingService.setWebHook(dingTalk.getWebHook());
        String response = null;
        if (Objects.equals(msgType, "text")) {
            response = sendDingService.sendText(jsonObject.getObject("msgBody", SendRequestParam.Text.class)).send(at);
        } else if (Objects.equals(msgType, "link")) {
            response = sendDingService.sendLink(jsonObject.getObject("msgBody", SendRequestParam.Link.class)).send(at);
        } else if (Objects.equals(msgType, "markdown")) {
            response = sendDingService.sendMarkdown(jsonObject.getObject("msgBody", SendRequestParam.Markdown.class)).send(at);
        } else if (Objects.equals(msgType, "actionCard")) {
            response = sendDingService.sendActionCard(jsonObject.getObject("msgBody", SendRequestParam.Actioncard.class));
        } else if (Objects.equals(msgType, "feedCard")) {
            response = sendDingService.sendFeedcard(jsonObject.getObject("msgBody", SendRequestParam.Feedcard.class));
        }
        JSONObject res = JSONObject.parseObject(response);
        if (res.getInteger("errcode") == 0)
            return new Result(true, "钉钉消息发送成功", null);
        else {
            return new Result(true, "钉钉消息发送失败" + res.getObject("errmsg", String.class), null);
        }
    }

}
