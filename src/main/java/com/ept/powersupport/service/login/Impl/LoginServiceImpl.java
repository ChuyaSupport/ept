package com.ept.powersupport.service.login.Impl;

import com.alibaba.fastjson.JSON;
import com.ept.powersupport.config.Constants;
import com.ept.powersupport.entity.User;
import com.ept.powersupport.repository.UserRepository;
import com.ept.powersupport.reqObj.UserInfo;
import com.ept.powersupport.service.login.LoginService;
import com.ept.powersupport.util.DBUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    @Autowired
    private DBUtil dbUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 检查用户是否登录
     * @param openid
     * @return
     */
    @Override
    public boolean checkIsLogin(String openid) {
        SqlSession session = dbUtil.getSqlSession();
        UserRepository userRepository = session.getMapper(UserRepository.class);
        List<User> list = null;
        try {
            list = userRepository.findByOpenid(openid);
            session.commit();
        }catch (Exception e) {
            log.error("[查询用户失败]");
        }

        if(!list.isEmpty()) {
            return true;
        }
        log.error("[验证用户身份失败]");
        return false;
    }

    /**
     * codeToOpenid
     * @param code
     * @return
     */
    @Override
    public String codeToOpenid(String code) {

        //请求微信服务器
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + Constants.WX_APPID + "&secret=" + Constants.WX_SECRET + "&js_code=" + code
                + "&grant_type=authorization_code";

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = null;
        Map map = null;
        String openid;
        try {
            response = okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response.isSuccessful()) {
            try {
                map = (Map) JSON.parse(response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        try {
            openid = map.get("openid").toString();
        }catch (Exception e) {
            log.error("[获取openid失败]");
            return null;
        }

        return openid;
    }

    /**
     * 检查用户是否注册
     * @param openid
     * @return
     */
    @Override
    public boolean checkIsUserRegisted(String openid) {

        HashOperations hashOperations = redisTemplate.opsForHash();

        if(hashOperations.hasKey("t_user", openid)) {
            return true;
        }

        log.info("[用户首次登录] openid = {}", openid);
        return false;
    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    @Override
    public boolean userRegist(User user) {

        //写入数据库
        SqlSession session = dbUtil.getSqlSession();
        UserRepository userRepository = session.getMapper(UserRepository.class);
        try {
            if(userRepository.save(user) != -1) {
                user = userRepository.findSingleUserByOpenid(user.getOpenid());
                session.commit();
                //写入redis
                HashOperations hashOperations = redisTemplate.opsForHash();
                hashOperations.put("t_user", user.getOpenid(), user);
                log.info("[用户注册完成] openid = {}", user.getOpenid());
            }else {
                log.error("[用户注册失败] openid = {}", user.getOpenid());
                return false;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 更新用户个人信息
     * @param userInfo
     * @return
     */
    @Override
    public boolean updateUserInfo(UserInfo userInfo) {




        return false;
    }

}
