package com.jnu.common.utils;

import org.springframework.util.DigestUtils;

/**
 * MD5工具类
 * @author duya001
 */
public class MD5Util {
    //掩，用于混交md5
//    private static final String slat = "&%5123***&&%%$$#@";
    /**
     * 生成md5
     * @param str 真实数据
     * @return
     */
    public static String getMD5(String str) {
//        String base = str +"/"+slat;
        String md5 = DigestUtils.md5DigestAsHex(str.getBytes());
        return md5;
    }
}
