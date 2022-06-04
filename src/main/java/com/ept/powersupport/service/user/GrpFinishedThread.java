package com.ept.powersupport.service.user;

import com.ept.powersupport.repository.UserRepository;
import com.ept.powersupport.util.DBUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

@Slf4j
public class GrpFinishedThread extends Thread{

    List joinIdList;
    String group_id;

    public GrpFinishedThread(List joinIdList, String group_id) {
        this.joinIdList = joinIdList;
        this.group_id = group_id;
    }

    @Override
    public void run() {

        int status;

        //更新数据库信息
        DBUtil dbUtil = new DBUtil();
        SqlSession session = dbUtil.getSqlSession();
        UserRepository userRepository = session.getMapper(UserRepository.class);

        try {
            status = userRepository.updateTGroupListStatus1(group_id);
            if(status != -1) {
                for(int i=0; i<joinIdList.size(); ++i) {
                    status = userRepository.updateTGroupDtlStatus1(String.valueOf(joinIdList.get(i)));
                    if(status == -1) {
                        break;
                    }
                }
            }

            if (status != -1) {
                session.commit();
                log.info("[Mysql::拼团信息更新完成] group_id = {}", group_id);
            }else {
                log.error("[Mysql::拼团信息更新失败] group_id = {}", group_id);
            }


        }catch (Exception e) {
            log.error("[Mysql::拼团信息更新失败] group_id = {}", group_id);
        }
    }
}
