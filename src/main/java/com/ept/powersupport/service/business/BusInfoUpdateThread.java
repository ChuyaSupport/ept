package com.ept.powersupport.service.business;

import com.ept.powersupport.entity.Business;
import com.ept.powersupport.repository.BusinessRepository;
import com.ept.powersupport.util.DBUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;


@Slf4j
public class BusInfoUpdateThread extends Thread{

    Business business;
    String business_id;

    public BusInfoUpdateThread(Business business, String business_id) {
        this.business = business;
        this.business_id = business_id;
    }

    @Override
    public void run() {
        int status = -1;
        DBUtil dbUtil = new DBUtil();
        SqlSession session = dbUtil.getSqlSession();
        BusinessRepository businessRepository = session.getMapper(BusinessRepository.class);

        try{
            status = businessRepository.update(business);
            session.commit();
        }catch (Exception e) {
            log.error("[更新商家信息出错] business_id = {}", business_id);
            e.printStackTrace();
        }

        if(status != -1) {
            log.info("[更新商家信息完成] business_id = {}", business_id);
        }

    }
}
