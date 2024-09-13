package com.jnu.dcinfo.jumpToMail.util;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;

public class HmacSha256Util
{
    /*
      钉钉自定义机器人安全设置
      把timestamp+"\n"+密钥当做签名字符串，使用HmacSHA256算法计算签名，然后进行Base64 encode，
      最后再把签名参数再进行urlEncode，得到最终的签名（需要使用UTF-8字符集）
      @param secret
     * @return
     */
    /**
     * 钉钉自定义机器人安全设置
     *
     * @param timestamp
     * @param secret
     * @return String
     */
    public static String dingHmacSHA256(long timestamp, String secret)
    {
        try
        {
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            return URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
        }
        catch (Exception e)
        {
            return null;
        }
    }
}

