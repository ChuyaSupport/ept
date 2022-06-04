package com.ept.powersupport.service.user;

import com.ept.powersupport.repository.UserRepository;
import com.ept.powersupport.util.DBUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;

@Slf4j
public class PaidStatusUpdateThread extends Thread{

    private String order_id;

    public PaidStatusUpdateThread(String order_id) {
        this.order_id = order_id;
    }

    @Override
    public void run() {
        DBUtil dbUtil = new DBUtil();
        SqlSession session = dbUtil.getSqlSession();
        UserRepository userRepository = session.getMapper(UserRepository.class);
        int status = 0;

        try {
            status = userRepository.updatePaidStatus(order_id);
        }catch (Exception e) {
            e.printStackTrace();
            log.error("[数据库 :: 支付信息更新失败] order_id = {}", order_id);
        }

        if (status != -1) {
            session.commit();
            log.info("[数据库 :: 支付信息已更新] order_id = {}", order_id);
        }else {
            log.error("[数据库 :: 支付信息更新失败] order_id = {}", order_id);
        }

    }
}
