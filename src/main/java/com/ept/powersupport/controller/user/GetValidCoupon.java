package com.ept.powersupport.controller.user;

import com.ept.powersupport.service.login.Impl.LoginServiceImpl;
import com.ept.powersupport.service.user.Impl.UserServiceImpl;
import com.ept.powersupport.util.DesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class GetValidCoupon {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private LoginServiceImpl loginService;

    @RequestMapping("/getValidCoupon")
    public Object getValidCoupon(@RequestParam String eptcode, @RequestParam String com_id) {

        //解码eptcode，获取openid
        String openid = DesUtil.decrypt(eptcode);

        //检查登录
        if(!loginService.checkIsLogin(openid)) {
            return null;
        }

        return userService.getCoupon(openid, com_id);
    }
}
