package com.ept.powersupport.service.redis.Impl;

import com.ept.powersupport.entity.Business;
import com.ept.powersupport.entity.ComImg;
import com.ept.powersupport.service.business.Impl.BusinessServiceImpl;
import com.ept.powersupport.service.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private Business business;

    @Autowired
    private BusinessServiceImpl businessService;


    @Override
    public boolean setLoginUser(String openid) {
        SetOperations<String, Object> setOperations = redisTemplate.opsForSet();
        setOperations.add("loginUser", openid);
        log.info("[用户登录成功] openid = {}", openid);
        return true;
    }

    @Override
    public boolean checkIsLogin(String openid) {
        SetOperations<String, Object> setOperations = redisTemplate.opsForSet();
        Set set = setOperations.members("loginUser");

        if(set.contains(openid)) {
            return true;
        }
        log.error("[用户未登录] openid = {}", openid);
        return false;
    }

    @Override
    public boolean updateBusinessInfo(Business bus) {

        HashOperations hashOperations = redisTemplate.opsForHash();

        //按手机号查询business
        business = businessService.getBusinessByPhoneNumber(bus.getPhone_number());
        System.out.println(business);

        //更新redis对应business
        try {
            hashOperations.put("t_business", String.valueOf(business.getBusiness_id()), business);
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
