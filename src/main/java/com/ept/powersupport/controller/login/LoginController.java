package com.ept.powersupport.controller.login;

import com.alibaba.fastjson.JSONObject;
import com.ept.powersupport.entity.User;
import com.ept.powersupport.reqObj.UserInfo;
import com.ept.powersupport.service.login.Impl.LoginServiceImpl;
import com.ept.powersupport.service.login.UserInfoUpdateThread;
import com.ept.powersupport.service.redis.Impl.RedisServiceImpl;
import com.ept.powersupport.util.DesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class LoginController {

    @Autowired
    private LoginServiceImpl loginService;

    @Autowired
    private User user;

    @Autowired
    private RedisServiceImpl redisService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserInfo usrInf;

    /**
     * 登录
//     * @param code
     * @return
     */
    @RequestMapping("/login")
    public String login(@RequestParam String code, @RequestParam String userInfo){

        //Json2Object
        usrInf = JSONObject.parseObject(userInfo, UserInfo.class);


        if(usrInf.getNickName() == null || usrInf.getAvatarUrl() == null) {
            return "Error";
        }


//      code转openid
        String openid = loginService.codeToOpenid(code);
        if(openid == null) {
            return "Error";
        }

        //检查用户是否注册，若未注册自动注册
        if(!loginService.checkIsUserRegisted(openid)) {

            //用户注册
            user.setOpenid(openid);
            user.setUser_name(usrInf.getNickName());
            user.setUser_profile(usrInf.getAvatarUrl());

            if(!loginService.userRegist(user)) {
                return "Error";
            }
        }

        //用户登录，添加openid到loginUser中
        if (redisService.setLoginUser(openid)) {

//            若用户资料有差异，进行更新
            Thread thread = new UserInfoUpdateThread(openid, usrInf, redisTemplate);
            thread.start();

            return DesUtil.encrypt(openid);
        }


        return "Error";
    }
}
