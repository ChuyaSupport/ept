package com.ept.powersupport.controller.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class BusinessInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        //设置编码
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "text/html; charset = utf-8");

        //登录检查
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for(Cookie cookie : cookies) {
                if(cookie.getName().equals("EPTCODE")) {
                    return true;
                }
            }
        }

        try{
            response.getWriter().write("请登录！");
        }catch (Exception e) {
            e.printStackTrace();
        }
        response.setHeader("refresh", "2;url=/test.html");

        log.error("[用户未登录]");
        return false;
    }
}
