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
public class GrpQuit {

    @Autowired
    private RedisServiceImpl redisService;

    @Autowired
    private UserServiceImpl userService;

    @RequestMapping("/grpQuit")
    public Object grpQuit(@RequestParam String eptcode, @RequestParam String group_id) {

        String openid = DesUtil.decrypt(eptcode);

        //检查登录
        if(!redisService.checkIsLogin(openid)) {
            return null;
        }

        return userService.QuitGrp(openid, group_id);
    }
}
