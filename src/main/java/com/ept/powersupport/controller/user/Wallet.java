package com.ept.powersupport.controller.user;

import com.ept.powersupport.service.redis.Impl.RedisServiceImpl;
import com.ept.powersupport.service.user.Impl.UserServiceImpl;
import com.ept.powersupport.util.DesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/user/wallet")
public class Wallet {

    @Autowired
    private RedisServiceImpl redisService;

    @Autowired
    public UserServiceImpl userService;

    /**
     * 余额查询
     * @param eptcode
     * @return
     */
    @RequestMapping("/ls")
    public BigDecimal lsBalance(@RequestParam String eptcode) {

        String openid = DesUtil.decrypt(eptcode);

        //检查登录
        if(!redisService.checkIsLogin(openid)) {
            return null;
        }

        return userService.balance(openid);
    }

    @RequestMapping("/topup")
    public BigDecimal recharge(@RequestParam String eptcode, @RequestParam BigDecimal amount) {
        String openid = DesUtil.decrypt(eptcode);

        //检查登录
        if(!redisService.checkIsLogin(openid)) {
            return null;
        }
        return userService.topup(openid, amount);
    }
}
