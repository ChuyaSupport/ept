package com.ept.powersupport.controller.user;

import com.ept.powersupport.service.login.Impl.LoginServiceImpl;
import com.ept.powersupport.service.user.Impl.UserServiceImpl;
import com.ept.powersupport.util.DesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/user")
public class FollowOption {


    @Autowired
    private LoginServiceImpl loginService;

    @Autowired
    private UserServiceImpl userService;

    @RequestMapping("/follow")
    public Object followAndRemove(@RequestParam String eptcode, @RequestParam String business_id, @RequestParam String option) {

        //解码eptcode，获取openid
        String openid = DesUtil.decrypt(eptcode);

        //检查登录
        if(!loginService.checkIsLogin(openid)) {
            return null;
        }

        if(option.equals("ls")) {
            if(userService.checkIsFollow(business_id, openid)) {
                return "true";
            }else {
                return "false";
            }

        }else if(option.equals("follow")) {
            if(userService.addFollow(business_id, openid)) {
                return "1";
            }else {
                return "0";
            }

        }else if(option.equals("remove")) {
            if(userService.removeFollow(business_id, openid)) {
                return "1";
            }else {
                return "0";
            }

        }else {
            log.error("[无效的option] option = {}", option);
            return null;
        }
    }
}
