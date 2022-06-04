package com.ept.powersupport.service.login;

import com.ept.powersupport.entity.User;
import com.ept.powersupport.reqObj.UserInfo;


public interface LoginService {

    //检查用户是否登录
    boolean checkIsLogin(String openid);

    //code To openid
    String codeToOpenid(String code);

    //检查用户是否注册
    boolean checkIsUserRegisted(String openid);

    //用户注册
    boolean userRegist(User user);

    //更新用户信息
    boolean updateUserInfo(UserInfo userInfo);

}
