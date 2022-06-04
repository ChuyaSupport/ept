package com.ept.powersupport.service.user;

import com.ept.powersupport.repository.UserRepository;
import com.ept.powersupport.util.DBUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;

@Slf4j
public class UnPaidOrderClean extends Thread{

    private String join_id;
    private String order_id;

    public UnPaidOrderClean(String join_id, String order_id) {
        this.join_id = join_id;
        this.order_id = order_id;
    }

    @Override
    public void run() {
        DBUtil dbUtil = new DBUtil();
        SqlSession session = dbUtil.getSqlSession();
        UserRepository userRepository = session.getMapper(UserRepository.class);

        try {
            userRepository.UnpaidDtlClean(join_id);
            userRepository.UnpaidOrderClean(order_id);
            session.commit();
        }catch (Exception e) {
            e.printStackTrace();
            log.error("[数据库 :: 未支付订单清理失败] order_id = {}", order_id);
        }

    }
}
