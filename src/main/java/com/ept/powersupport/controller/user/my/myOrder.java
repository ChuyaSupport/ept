package com.ept.powersupport.controller.user.my;


import com.ept.powersupport.service.login.Impl.LoginServiceImpl;
import com.ept.powersupport.service.user.Impl.UserServiceImpl;
import com.ept.powersupport.util.DesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/my")
public class myOrder {

    @Autowired
    private LoginServiceImpl loginService;

    @Autowired
    private UserServiceImpl userService;

    @RequestMapping("/order")
    public Object my_order(@RequestParam String eptcode) {

        //解码eptcode，获取openid
        String openid = DesUtil.decrypt(eptcode);

        //检查登录
        if(!loginService.checkIsLogin(openid)) {
            return null;
        }

        return userService.myOrder(openid);
    }
}
