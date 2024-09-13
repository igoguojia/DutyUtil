package com.jnu.dcinfo.jumpToMail.service;

import com.jnu.dcinfo.jumpToMail.pojo.SendRequestParam;
import com.jnu.dcinfo.jumpToMail.service.serviceImpl.SendDingServiceImpl;

public interface SendDingService {
    public SendDingServiceImpl sendText(SendRequestParam.Text text);

    public SendDingServiceImpl sendLink(SendRequestParam.Link link);

    public SendDingServiceImpl sendMarkdown(SendRequestParam.Markdown markdown);

    public String sendActionCard(SendRequestParam.Actioncard actioncard);

    public String sendFeedcard(SendRequestParam.Feedcard feedcard);

    public String send(SendRequestParam.At at);

    public String send();

    public void setWebHook(String webHook);

    public void setSecret(String secret);
}
