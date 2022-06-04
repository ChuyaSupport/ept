package com.ept.powersupport.controller.business;

import com.ept.powersupport.service.business.Impl.BusinessServiceImpl;
import com.ept.powersupport.util.DesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/business")
public class Logout {

    @Autowired
    private HttpServletResponse httpServletResponse;

    @Autowired
    private BusinessServiceImpl businessService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @RequestMapping("/logout")
    private void busLogout() {

        //设置编码
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setHeader("Content-Type", "text/html; charset = utf-8");

        String business_id = null;

        Cookie[] cookies = httpServletRequest.getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("EPTCODE")) {
                business_id = DesUtil.decrypt(cookie.getValue());
                System.out.println(business_id);
            }
        }

        if(businessService.businessLogout(business_id)) {
            try{
                httpServletResponse.getWriter().write("注销成功");
            }catch (Exception e) {
                e.printStackTrace();
            }
            httpServletResponse.setHeader("refresh", "2;url=/test.html");
            return;
        }else {
            try{
                httpServletResponse.getWriter().write("当前未登录账号");
            }catch (Exception e) {
                e.printStackTrace();
            }
            httpServletResponse.setHeader("refresh", "2;url=/test.html");
            return;
        }
    }
}
