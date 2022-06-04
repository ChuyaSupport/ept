package com.ept.powersupport.service.user;

import com.ept.powersupport.entity.User;
import com.ept.powersupport.repository.UserRepository;
import com.ept.powersupport.util.DBUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;


/**
 * 更新用户位置信息
 */
@Slf4j
public class UserInfoUpdateThread extends Thread{

    private User user;

    public UserInfoUpdateThread(User user) {
        this.user = user;
    }

    @Override
    public void run() {
        DBUtil dbUtil = new DBUtil();
        SqlSession session = dbUtil.getSqlSession();
        UserRepository userRepository = session.getMapper(UserRepository.class);

        try {
            if(userRepository.update(user) != -1) {
                session.commit();
                log.info("[用户信息已更新] openid = {}, Logitude = {}, Latitude = {}",
                        user.getOpenid(), user.getUser_longitude(), user.getUser_latitude());
                return;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        log.info("[用户信息更新失败] openid = {}", user.getOpenid());
    }
}
