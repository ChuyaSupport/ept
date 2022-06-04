package com.ept.powersupport.controller.user;

import com.ept.powersupport.service.redis.Impl.RedisServiceImpl;
import com.ept.powersupport.service.user.Impl.UserServiceImpl;
import com.ept.powersupport.util.DesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class GrpJoin {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private RedisServiceImpl redisService;

    @RequestMapping("/grpJoin")
    public Object grpJoin(@RequestParam String eptcode, @RequestParam String group_id, @RequestParam String coupon_id,
                          @RequestParam String addr_id, @RequestParam String note, @RequestParam String tableware) {

        String openid = DesUtil.decrypt(eptcode);

        //检查登录
        if(!redisService.checkIsLogin(openid)) {
            return null;
        }
        return userService.joinGroup(openid, group_id, coupon_id, addr_id, note, tableware);
    }

}
