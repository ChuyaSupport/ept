package com.ept.powersupport.service.user;


import com.ept.powersupport.entity.UserAddr;
import com.ept.powersupport.repository.UserRepository;
import com.ept.powersupport.util.DBUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;

@Slf4j
public class NewAddrThread extends Thread{

    private UserAddr userAddr;
    private String updateUserAddrId;

    public NewAddrThread(UserAddr userAddr, String updateUserAddrId) {
        this.userAddr = userAddr;
        this.updateUserAddrId = updateUserAddrId;
    }



    @Override
    public void run() {

        DBUtil dbUtil = new DBUtil();
        SqlSession session = dbUtil.getSqlSession();
        UserRepository userRepository = session.getMapper(UserRepository.class);
        int status = 0;

        try {
            status = userRepository.newAddr(userAddr);
            if(status != -1) {
                status = userRepository.updateUserAddrStatus(updateUserAddrId);
            }
            session.commit();
        }catch (Exception e) {
            log.info("[数据库::收货信息写入失败] addr_id = {}", userAddr.getAddr_id());
            e.printStackTrace();
        }

        if(status != -1)
            log.info("[数据库::收货信息添加成功]");

    }
}
