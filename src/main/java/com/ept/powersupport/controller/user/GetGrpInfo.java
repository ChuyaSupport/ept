package com.ept.powersupport.controller.user;

import com.ept.powersupport.resObj.ResGroupInfo;
import com.ept.powersupport.service.redis.Impl.RedisServiceImpl;
import com.ept.powersupport.service.user.Impl.UserServiceImpl;
import com.ept.powersupport.util.DesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/user")
public class GetGrpInfo {

    @Autowired
    private RedisServiceImpl redisService;

    @Autowired
    private UserServiceImpl userService;

    @RequestMapping("/getGrpInfo")
    public Object grpInfo(@RequestParam String eptcode, @RequestParam String com_id) {

        log.info("[拼团信息查询]");
        //解码eptcode，获取openid

        String openid = DesUtil.decrypt(eptcode);

        //检查登录
        if(!redisService.checkIsLogin(openid)) {
            return null;
        }

        List resGroupInfo = userService.getGrpInfo(openid, com_id);

        return resGroupInfo;
    }
}
