package com.ept.powersupport.service.user;

import com.ept.powersupport.repository.UserRepository;
import com.ept.powersupport.util.DBUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;

@Slf4j
public class DelAddrThread extends Thread{

    private String addr_id;

    public DelAddrThread(String addr_id) {
        this.addr_id = addr_id;
    }

    @Override
    public void run() {
        DBUtil dbUtil = new DBUtil();
        SqlSession session = dbUtil.getSqlSession();
        UserRepository userRepository = session.getMapper(UserRepository.class);
        int status = 0;

        try {
            status = userRepository.delAddr(addr_id);
        }catch (Exception e) {
            e.printStackTrace();
            log.error("[数据库::删除收货地址信息失败] addr_id = {}", addr_id);
        }

        if(status != -1) {
            session.commit();
            log.info("[数据库::收货地址信息已删除] addr_id = {}", addr_id);
        }else {
            log.error("[数据库::删除收货地址信息失败] addr_id = {}", addr_id);
        }
    }
}
