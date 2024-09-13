package com.jnu.tokenHandle.service.impl;

import com.jnu.tokenHandle.pojo.AccessLevel;
import com.jnu.tokenHandle.util.JWTUtil;
import com.jnu.tool.utils.ResultEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class HeaderTokenInterceptor implements HandlerInterceptor {
    private static int lenAuth; // IO权限长度

    @Value("${token.payload.lenAuth}")
    public void setLenAuth(String lenAuth) {
        HeaderTokenInterceptor.lenAuth = Integer.parseInt(lenAuth);
    }

    @Override
    public boolean preHandle(@NotNull HttpServletRequest httpServletRequest, @NotNull HttpServletResponse httpServletResponse,
                             @NotNull Object handler) throws Exception {
        String responseData = null;
        String auth=null;
        List<String> authChara=null;
        httpServletResponse.setCharacterEncoding("UTF-8");
        Map<String, String> tokenMap;
        // 获取我们请求头中的token验证字符
        String headerToken = httpServletRequest.getHeader("token");
//        if (!httpServletRequest.getRequestURI().contains("login")) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        String apiName = method.getName();
//        if (apiName.equals("sendDingTalk"))
//            return true;
        try {
            auth = AccessLevel.valueOf(apiName).getAuth();
            authChara = Arrays.asList(AccessLevel.valueOf(apiName).getAuthChara());
        }catch (IllegalArgumentException e){
            return true;
        }
        if (headerToken == null) {
            // 如果token不存在的话,返回错误信息。
            responseData = ResultEntity.failWithoutData("非法的token！").returnResult();
            httpServletResponse.getWriter().write(responseData);
            return false;
        }

        // 对token进行验证
        tokenMap = JWTUtil.verifyToken(headerToken);
        if (!Objects.equals(tokenMap.get("success"), "true")) {
            // 当token验证出现异常返回错误信息,token不匹配
            responseData = ResultEntity.failWithoutData("token认证失败！").returnResult();
            httpServletResponse.getWriter().write(responseData);
            return false;
        }
        String[] authIDList = tokenMap.get(JWTUtil.authID).split(" ");
        outLoop:
        {
            innerLoop:
            for (String authID : authIDList) {
                if (authID.charAt(lenAuth - 1) != '1') {
                    responseData = ResultEntity.failWithoutData("异常权限！").returnResult();
                    break outLoop;
                }
                if (authChara.contains(authID.substring(lenAuth))) {
                    for (int i = 0; i < lenAuth; i++) {
                        if (auth.charAt(i) > authID.charAt(i)) {
//                                responseData = ResultEntity.failWithoutData("权限不足！").returnResult();
                            continue innerLoop;
                        }
                    }
                    break outLoop;
                }
            }
            responseData = ResultEntity.failWithoutData("权限不足！").returnResult();
        }
//        }


        if (responseData != null) {//如果有错误信息
            httpServletResponse.getWriter().write(responseData);
            return false;
        } else {
            // 将token加入返回页面的header
//            httpServletResponse.setHeader("token", headerToken);
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
                           ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                Object o, Exception e) throws Exception {
    }


}
