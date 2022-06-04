package com.ept.powersupport.service.login;


import com.ept.powersupport.entity.User;
import com.ept.powersupport.repository.UserRepository;
import com.ept.powersupport.reqObj.UserInfo;
import com.ept.powersupport.util.DBUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;


@Slf4j
public class UserInfoUpdateThread extends Thread{

    private String openid;
    private UserInfo userInfo;
    private RedisTemplate redisTemplate;



    //构造方法
    public UserInfoUpdateThread(String openid, UserInfo userInfo, RedisTemplate redisTemplate) {
        this.userInfo = userInfo;
        this.openid = openid;
        this.redisTemplate = redisTemplate;
    }

    //重写run方法
    @Override
    public void run() {

        HashOperations hashOperations = redisTemplate.opsForHash();
        User user = (User)hashOperations.get("t_user", openid);
        int status = 0;
        //若资料不一致进行更新
        if(!user.getUser_name().equals(userInfo.getNickName()) || !user.getUser_profile().equals(userInfo.getAvatarUrl())) {
            user.setUser_name(userInfo.getNickName());
            user.setUser_profile(userInfo.getAvatarUrl());

            hashOperations.put("t_user", openid, user);
            DBUtil dbUtil = new DBUtil();
            SqlSession session = dbUtil.getSqlSession();
            UserRepository userRepository = session.getMapper(UserRepository.class);

            try {
                status = userRepository.update(user);
                session.commit();
            }catch (Exception e) {
                log.error("[更新用户资料失败] openid = {}", openid);
                e.printStackTrace();
            }

            if(status == 1) {
                log.info("[用户资料已更新] openid = {}", openid);
            }

        }
    }
}
