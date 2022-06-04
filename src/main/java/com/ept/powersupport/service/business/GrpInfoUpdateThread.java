package com.ept.powersupport.service.business;

import com.ept.powersupport.entity.Group;
import com.ept.powersupport.repository.BusinessRepository;
import com.ept.powersupport.util.DBUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;

@Slf4j
public class GrpInfoUpdateThread extends Thread{

    private Group group;

    public GrpInfoUpdateThread(Group group){
     this.group = group;
    }

    @Override
    public void run() {
        DBUtil dbUtil = new DBUtil();
        SqlSession session = dbUtil.getSqlSession();

        BusinessRepository businessRepository = session.getMapper(BusinessRepository.class);

        try{
            businessRepository.setGrp(group);
            session.commit();
        }catch (Exception e) {
            log.error("[拼团信息写入数据库失败]");
            e.printStackTrace();
        }
    }

}
