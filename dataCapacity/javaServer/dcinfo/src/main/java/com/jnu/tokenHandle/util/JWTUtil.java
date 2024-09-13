package com.jnu.tokenHandle.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


/**
 * jwt工具类
 *
 * @ClassName: JWTUtil
 * @author duya001
 */
@Component
public class JWTUtil {
    /**
     * sercetKey token加密时使用的密钥
     * userID payload里键值对键的名称 获取用户id
     * authID payload里键值对键的名称 获取用户权限
     */
    public static String sercetKey;
    public static String userID;
    public static String authID;

    @Value("${token.SECRET}")
    public void setSECRET(String SECRET) {
        JWTUtil.sercetKey = SECRET;
    }

    @Value("${token.payload.userID}")
    public void setUserID(String userID) {
        JWTUtil.userID = userID;
    }

    @Value("${token.payload.authID}")
    public void setAuthID(String authID) {
        JWTUtil.authID = authID;
    }

    /**
     * 将token解密出来,将payload信息包装成Claims类返回
     *
     * @throws: JWTVerificationException
     * @Title: verifyToken
     * @param: @param token
     * @param: @return
     * @return: Map : payload
     */
    public static Map<String, String> verifyToken(String token) {
        Map<String, String> payload = new HashMap<>();
        try {
            Algorithm algorithm = Algorithm.HMAC256(sercetKey);
            JWTVerifier verifier = JWT.require(algorithm).build(); //Reusable verifier instance
            DecodedJWT jwt = verifier.verify(token);
            String _userID = jwt.getClaim(userID).asString();
            String _authID = jwt.getClaim(authID).asString();
            if (_userID == null || _authID == null)
                payload.put("success", "false");
            else {
                payload.put("success", "true");
                payload.put(userID, _userID);
                payload.put(authID, _authID);
            }
        } catch (JWTVerificationException exception) {
            //Invalid signature/claims
            payload.put("success", "false");
        }
        return payload;

    }
}
