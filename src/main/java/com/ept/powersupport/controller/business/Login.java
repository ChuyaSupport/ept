package com.ept.powersupport.controller.business;

import com.ept.powersupport.entity.Business;
import com.ept.powersupport.service.business.Impl.BusinessServiceImpl;
import com.ept.powersupport.util.DesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/business")
public class Login {

    @Autowired
    private HttpServletResponse httpServletResponse;

    @Autowired
    private BusinessServiceImpl businessService;

    @Autowired
    private Business business;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @RequestMapping("/login")
    public void busLogin(@RequestParam String phone_number, @RequestParam String business_passwd) {

        //设置编码
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setHeader("Content-Type", "text/html; charset = utf-8");

        if(phone_number.equals("") || business_passwd.equals("")) {
            try{
                httpServletResponse.getWriter().write("输入有误，请检查！");
            }catch (Exception e) {
                e.printStackTrace();
            }
            httpServletResponse.setHeader("refresh", "2;url=/test.html");
            return;
        }

        Cookie[] cookies = httpServletRequest.getCookies();
        if(cookies != null) {
            for(Cookie cookie : cookies) {
                if(cookie.getName().equals("EPTCODE")) {
                    try{
                        httpServletResponse.getWriter().write("请先注销当前账号！");
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    httpServletResponse.setHeader("refresh", "2;url=/test.html");
                    return;
                }
            }
        }


        business.setPhone_number(phone_number);
        business.setBusiness_passwd(business_passwd);

        if(businessService.businessLogin(business)) {

            try{
                httpServletResponse.getWriter().write("登录成功！");
            }catch (Exception e) {
                e.printStackTrace();
            }
            httpServletResponse.setHeader("refresh", "2;url=/test.html");
            return;
        } else {
            try{
                httpServletResponse.getWriter().write("用户名或密码有误，请检查！");
            }catch (Exception e) {
                e.printStackTrace();
            }
            httpServletResponse.setHeader("refresh", "2;url=/test.html");
            return;
        }

    }
}
