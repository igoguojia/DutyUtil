package com.jnu.dcinfo.jumpToMail.pojo;

/**
 * @author duya001
 * 
 */
public class DingTalk {
    // Webhook 地址
    private String webHook;
    // 群组名称
    private String groupName;
    // 加签类型的密钥
    private String secret;

    public DingTalk() {
    }

    public DingTalk(String webHook, String groupName, String secret) {
        this.webHook = webHook;
        this.groupName = groupName;
        this.secret = secret;
    }

    public String getWebHook() {
        return webHook;
    }

    public void setWebHook(String webHook) {
        this.webHook = webHook;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Override
    public String toString() {
        return "DingTalk{" +
                "webHook='" + webHook + '\'' +
                ", groupName='" + groupName + '\'' +
                ", secret='" + secret + '\'' +
                '}';
    }
}
