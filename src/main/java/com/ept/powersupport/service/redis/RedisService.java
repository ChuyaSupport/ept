package com.ept.powersupport.service.redis;

import com.ept.powersupport.entity.Business;

public interface RedisService {
    boolean setLoginUser(String openid);
    boolean checkIsLogin(String openid);
    boolean updateBusinessInfo(Business business);
}
