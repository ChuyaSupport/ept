package com.ept.powersupport.controller.user;

import com.ept.powersupport.service.login.Impl.LoginServiceImpl;
import com.ept.powersupport.service.user.Impl.UserServiceImpl;
import com.ept.powersupport.service.user.UserService;
import com.ept.powersupport.util.DesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class PayCalcel {

    @Autowired
    private LoginServiceImpl loginService;

    @Autowired
    private UserServiceImpl userService;

    @RequestMapping("/order/cancel")
    public Object orderCalcel(@RequestParam String eptcode, @RequestParam String order_id) {

        //解码eptcode，获取openid
        String openid = DesUtil.decrypt(eptcode);

        //检查登录
        if(!loginService.checkIsLogin(openid)) {
            return null;
        }

        return userService.payCancel(order_id);
    }
}
