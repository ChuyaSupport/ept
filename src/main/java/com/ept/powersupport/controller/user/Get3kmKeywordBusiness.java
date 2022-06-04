package com.ept.powersupport.controller.user;

import com.ept.powersupport.entity.Business;
import com.ept.powersupport.entity.User;
import com.ept.powersupport.service.redis.Impl.RedisServiceImpl;
import com.ept.powersupport.service.user.Impl.UserServiceImpl;
import com.ept.powersupport.util.DesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@ResponseBody
@Slf4j
@RequestMapping("/user")
public class Get3kmKeywordBusiness {

    @Autowired
    private User user;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private RedisServiceImpl redisService;


    /**
     * 获取三千米关键字商家
     * @param eptcode
     * @param key_word
     * @return
     */
    @RequestMapping("/searchByKeyword")
    private List get3kmKeywordBusiness(@RequestParam String eptcode, @RequestParam String key_word) {

        List<Business> list;
        List resList;
        List retList;

        //解码eptcode，获取openid
        String openid = DesUtil.decrypt(eptcode);

        //检查登录
        if(!redisService.checkIsLogin(openid)) {
            return null;
        }

        //获取User
        user = userService.getUserByopenid(openid);

        if(user == null) {
            return null;
        }

        //获取3km商家查询列表
        list = userService.getBusinessInfo(user);

        //获取3km内商家
        resList = userService.get3kmBusiness(list, user);


        //获取含keyword关键字商家
        retList = userService.getKeywordBusiness(resList, key_word);

        if(retList.isEmpty()) {
            retList.add("null");
        }

        return retList;
    }

}
