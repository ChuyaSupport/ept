package com.ept.powersupport.controller.user;

import com.ept.powersupport.resObj.ShopDtl;
import com.ept.powersupport.service.redis.Impl.RedisServiceImpl;
import com.ept.powersupport.service.user.Impl.UserServiceImpl;
import com.ept.powersupport.util.DesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@ResponseBody
@Slf4j
public class GetShopDtl {

    @Autowired
    private RedisServiceImpl redisService;

    @Autowired
    private ShopDtl shopDtl;

    @Autowired
    private UserServiceImpl userService;


    /**
     * 获取店内详细信息
     * @param eptcode
     * @param business_id
     * @return
     */
    @RequestMapping("/getShopDtl")
    public Object GetShopDtl(@RequestParam String eptcode, @RequestParam String business_id) {

        log.info("[店内信息访问]");
        //解码eptcode，获取openid

        String openid = DesUtil.decrypt(eptcode);

        //检查登录
        if(!redisService.checkIsLogin(openid)) {
            return null;
        }

        //商品详情页
        shopDtl = userService.getShopDtl(business_id, userService.getUserByopenid(openid));

        return shopDtl;
    }
}
