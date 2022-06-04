package com.ept.powersupport.service.user;

import com.ept.powersupport.entity.Follow;
import com.ept.powersupport.repository.UserRepository;
import com.ept.powersupport.util.DBUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;

@Slf4j
public class AddFollowThread extends Thread{

    Follow follow;

    public AddFollowThread(Follow follow) {
        this.follow = follow;
    }

    @Override
    public void run() {
        int status = 0;

        DBUtil dbUtil = new DBUtil();
        SqlSession session = dbUtil.getSqlSession();
        UserRepository userRepository = session.getMapper(UserRepository.class);

        try{
            status = userRepository.addFollow(follow);
            session.commit();
        }catch (Exception e) {
            log.error("[关注信息更新失败] business_id = {}, openid = {}", follow.getBusiness_id(), follow.getOpenid());
        }
        if(status != -1) {
            log.info("[关注信息已更新] business_id = {}, openid = {}", follow.getBusiness_id(), follow.getOpenid());
        }else {
            log.error("[关注信息更新失败] business_id = {}, openid = {}", follow.getBusiness_id(), follow.getOpenid());
        }

    }
}
