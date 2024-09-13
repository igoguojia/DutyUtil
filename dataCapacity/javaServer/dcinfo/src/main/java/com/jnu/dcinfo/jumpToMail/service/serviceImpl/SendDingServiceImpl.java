package com.jnu.dcinfo.jumpToMail.service.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.jnu.dcinfo.jumpToMail.pojo.SendRequestParam;
import com.jnu.dcinfo.jumpToMail.service.SendDingService;
import com.jnu.dcinfo.jumpToMail.util.HmacSha256Util;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Data
public class SendDingServiceImpl implements SendDingService {
    //    @Value("${ding.robot.webhook}")
    private String robotWebhook;

    //    @Value("${ding.robot.key}")
    private String robotKey;

    @Autowired
    private RestTemplate restTemplate;

    private final static String MSG_TYPE_TEXT = "text";
    private final static String MSG_TYPE_LINK = "link";
    private final static String MSG_TYPE_MARKDOWN = "markdown";
    private final static String MSG_ACTION_CARD = "actionCard";
    private final static String MSG_FEED_CARD = "feedCard";

    private final ThreadLocal<SendRequestParam> paramLocal = new ThreadLocal<>();

    /**
     * 发送文本消息
     *
     * @param text
     * @return OapiRobotSendRequest
     */
    public SendDingServiceImpl sendText(SendRequestParam.Text text) {
        SendRequestParam request = new SendRequestParam();
        request.setText(text);
        request.setMsgtype(MSG_TYPE_TEXT);
        paramLocal.set(request);
        return this;
    }

    /**
     * 发送带连接的消息
     *
     * @param link
     * @return OapiRobotSendRequest
     */
    public SendDingServiceImpl sendLink(SendRequestParam.Link link) {
        SendRequestParam request = new SendRequestParam();
        request.setMsgtype(MSG_TYPE_LINK);
        request.setLink(link);
        paramLocal.set(request);
        return this;
    }

    /**
     * 发送markdown消息
     *
     * @param markdown
     * @return OapiRobotSendRequest
     */
    public SendDingServiceImpl sendMarkdown(SendRequestParam.Markdown markdown) {
        SendRequestParam request = new SendRequestParam();
        request.setMsgtype(MSG_TYPE_MARKDOWN);
        request.setMarkdown(markdown);
        paramLocal.set(request);
        return this;
    }

    public String sendActionCard(SendRequestParam.Actioncard actioncard) {
        SendRequestParam request = new SendRequestParam();
        request.setMsgtype(MSG_ACTION_CARD);
        request.setActionCard(actioncard);
        paramLocal.set(request);
        return this.send();
    }


    public String sendFeedcard(SendRequestParam.Feedcard feedcard) {
        SendRequestParam request = new SendRequestParam();
        request.setMsgtype(MSG_FEED_CARD);
        request.setFeedCard(feedcard);
        paramLocal.set(request);
        return this.send();
    }

    @Override
    public void setWebHook(String webHook) {
        this.robotWebhook = webHook;
    }

    @Override
    public void setSecret(String secret) {
        this.robotKey = secret;
    }


    /**
     * 推送钉钉机器人消息
     *
     * @return
     */
    public String send() {
        return this.send(null);
    }

    /**
     * 推送钉钉机器人消息
     *
     * @return
     */
    public String send(SendRequestParam.At at) {
        String json = null;
        String dingUrl = null;
        try {
            dingUrl = this.getDingUrl();
            //组装请求内容
            SendRequestParam param = paramLocal.get();
            param.setAt(at);
            json = JSON.toJSONString(param);
            return sendRequest(dingUrl, json);
        } catch (Exception e) {
            return null;
        } finally {
            paramLocal.remove();
        }
    }

    /**
     * 获取 钉钉机器人地址
     *
     * @param
     * @return String
     */
    private String getDingUrl() {
        long timestamp = System.currentTimeMillis();
        String sign = HmacSha256Util.dingHmacSHA256(System.currentTimeMillis(), robotKey);
        // 钉钉机器人地址（配置机器人的 webhook） https://oapi.dingtalk.com/robot/send?access_token=XXXXXX&timestamp=XXX&sign=XXX
        return String.format(robotWebhook + "&timestamp=%d&sign=%s", timestamp, sign);
    }

    private String sendRequest(String url, String params) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>(params, headers);
        ResponseEntity<String> entity = restTemplate.postForEntity(url, httpEntity, String.class);
        return entity.getBody();
    }
}

