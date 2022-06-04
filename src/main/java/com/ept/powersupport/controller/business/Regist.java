package com.ept.powersupport.controller.business;

import com.ept.powersupport.entity.Business;
import com.ept.powersupport.service.business.Impl.BusinessServiceImpl;
import com.ept.powersupport.service.redis.Impl.RedisServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/business")
public class Regist {

    @Autowired
    private BusinessServiceImpl businessService;

    @Autowired
    private Business business;

    @Autowired
    private RedisServiceImpl redisService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    /**
     * 商家注册
     * @param phone_number
     * @param business_passwd
     */
    @PostMapping("/regist")
    public void busReg(@RequestParam String phone_number, @RequestParam String business_passwd) {

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

        //检查账号是否存在
        if(businessService.checkIsExisted(phone_number)) {
            try{
                httpServletResponse.getWriter().write("注册失败::该手机号码已注册");
            }catch (Exception e) {
                e.printStackTrace();
            }
            httpServletResponse.setHeader("refresh", "2;url=/test.html");
            return;
        }

        //账号注册(写入数据库，更新redis)
        business.setPhone_number(phone_number);
        business.setBusiness_passwd(business_passwd);
        if(!businessService.businessRegist(business)) {
            try{
                httpServletResponse.getWriter().write("注册失败::写入数据库出错");
            }catch (Exception e) {
                e.printStackTrace();
            }
            httpServletResponse.setHeader("refresh", "2;url=/test.html");
            return;
        }
        if(!redisService.updateBusinessInfo(business)) {
            try{
                httpServletResponse.getWriter().write("注册失败::更新缓存出错");
            }catch (Exception e) {
                e.printStackTrace();
            }
            httpServletResponse.setHeader("refresh", "2;url=/test.html");
            return;
        }

        try{
            httpServletResponse.getWriter().write("注册成功");
        }catch (Exception e) {
            e.printStackTrace();
        }
        httpServletResponse.setHeader("refresh", "2;url=/test.html");
    }

}
