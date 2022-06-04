package com.ept.powersupport.controller.user;

import com.ept.powersupport.service.redis.Impl.RedisServiceImpl;
import com.ept.powersupport.service.user.Impl.UserServiceImpl;
import com.ept.powersupport.util.DesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class Pay {

    @Autowired
    private RedisServiceImpl redisService;

    @Autowired
    private UserServiceImpl userService;

    @RequestMapping("/pay")
    public Object isPaid(String eptcode, String order_id) {

        String openid = DesUtil.decrypt(eptcode);

        //检查登录
        if(!redisService.checkIsLogin(openid)) {
            return null;
        }

        return userService.pay(openid, order_id);
    }
}
