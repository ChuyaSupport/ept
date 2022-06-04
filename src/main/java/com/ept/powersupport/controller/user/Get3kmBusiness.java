package com.ept.powersupport.controller.user;

import com.ept.powersupport.entity.Business;
import com.ept.powersupport.entity.User;
import com.ept.powersupport.service.redis.Impl.RedisServiceImpl;
import com.ept.powersupport.service.user.Impl.UserServiceImpl;
import com.ept.powersupport.service.user.UserInfoUpdateThread;
import com.ept.powersupport.util.DesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@ResponseBody
@Slf4j
@RequestMapping("/user")
public class Get3kmBusiness {

    @Autowired
    private User user;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private RedisServiceImpl redisService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取3千米内商家信息
     * @param eptcode
     * @param user_longitude
     * @param user_latitude
     * @return
     */
    @RequestMapping("/getBusinessAround")
    public List get3kmBusiness(@RequestParam String eptcode, @RequestParam String user_longitude, @RequestParam String user_latitude) {

        //解码eptcode，获取openid
        String openid = DesUtil.decrypt(eptcode);

        //检查登录
        if(!redisService.checkIsLogin(openid)) {
            log.error("[用户未登录] openid = {}", openid);
            return null;
        }

        List<Business> list;
        List resList;
        HashOperations hashOperations = redisTemplate.opsForHash();
        user = (User)hashOperations.get("t_user", openid);
        user.setUser_longitude(BigDecimal.valueOf(Double.valueOf(user_longitude)));
        user.setUser_latitude(BigDecimal.valueOf(Double.valueOf(user_latitude)));


        //更新用户信息
        //redis
        userService.redisUserInfoUpdate(user);

        //更新数据库
        Thread thread = new UserInfoUpdateThread(user);
        thread.start();

        //获取3km商家查询列表
        list = userService.getBusinessInfo(user);

        //获取3km内商家信息
        resList = userService.get3kmBusiness(list, user);

        if(resList.isEmpty()) {
            resList.add("null");
        }

        return resList;
    }
}
