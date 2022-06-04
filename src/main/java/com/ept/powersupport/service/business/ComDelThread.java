package com.ept.powersupport.service.business;

import com.ept.powersupport.repository.BusinessRepository;
import com.ept.powersupport.util.DBUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

@Slf4j
public class ComDelThread extends Thread{

    private List joinIdList;
    private List groupIdList;
    private List imgIdList;
    private String com_id;

    public ComDelThread(List joinIdList, List groupIdList, List imgIdList, String com_id) {
        this.joinIdList = joinIdList;
        this.groupIdList = groupIdList;
        this.imgIdList = imgIdList;
        this.com_id = com_id;

    }

    @Override
    public void run() {
        DBUtil dbUtil = new DBUtil();
        SqlSession session = dbUtil.getSqlSession();
        BusinessRepository businessRepository = session.getMapper(BusinessRepository.class);
        int status = 0;

        //预留    --  删除用户收藏
        //预留    --  删除评论信息
        try {
            //删除团详情表信息
            for(int i=0; i<joinIdList.size(); ++i) {
                businessRepository.delFromGrpDtl((String) joinIdList.get(i));
            }
            //删除拼团表信息
            for(int i=0; i<groupIdList.size(); ++i) {
                businessRepository.delFromGrpList((String) groupIdList.get(i));
            }
            //删除商品图片
            for(int i=0; i<imgIdList.size(); ++i) {
                businessRepository.delFromComImg(imgIdList.get(i).toString());
            }
            //删除商品
            status = businessRepository.delFromCommodity(com_id);

        }catch (Exception e) {
            e.printStackTrace();
            log.error("[删除数据库信息失败] com_id = {}", com_id);
        }

        if(status != -1) {
            session.commit();
            log.info("[数据库信息已删除] com_id = {}", com_id);
        }
        else {
            log.error("[删除数据库信息失败] com_id = {}", com_id);
        }


    }
}
