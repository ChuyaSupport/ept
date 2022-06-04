package com.ept.powersupport.service.user;

import com.ept.powersupport.entity.Order;
import com.ept.powersupport.repository.UserRepository;
import com.ept.powersupport.util.DBUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;

@Slf4j
public class NewOrderThread extends Thread{

    private Order order;


    public NewOrderThread(Order order) {
        this.order = order;
    }

    @Override
    public void run() {
        DBUtil dbUtil = new DBUtil();
        SqlSession session = dbUtil.getSqlSession();
        UserRepository userRepository = session.getMapper(UserRepository.class);
        int status = 0;
        try {
            status = userRepository.newOrder(order);

        }catch (Exception e) {
            log.error("[订单信息写入数据库失败] order_id = {}", order.getOrder_id());
            e.printStackTrace();
        }
        if(status != -1){
            session.commit();
            log.info("[订单信息已写入数据库] order_id = {}", order.getOrder_id());
        }else {
            log.error("[订单信息写入数据库失败] order_id = {}", order.getOrder_id());
        }

    }
}
