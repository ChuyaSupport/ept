package com.ept.powersupport.service.user;

import com.ept.powersupport.entity.GroupDtl;
import com.ept.powersupport.repository.UserRepository;
import com.ept.powersupport.util.DBUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;

@Slf4j
public class AddGrpDtlThread extends Thread{

    private GroupDtl groupDtl;

    public AddGrpDtlThread(GroupDtl groupDtl) {
        this.groupDtl = groupDtl;
    }

    @Override
    public void run() {
        DBUtil dbUtil = new DBUtil();
        SqlSession session = dbUtil.getSqlSession();
        UserRepository userRepository = session.getMapper(UserRepository.class);
        int status;

        try {
            status = userRepository.addGrpDtl(groupDtl);
            session.commit();
        }catch (Exception e) {
            log.error("[拼团信息写入数据库失败] openid = {}", groupDtl.getOpenid());
            return;
        }

        if(status == -1) {
            log.error("[拼团信息写入数据库失败] openid = {}", groupDtl.getOpenid());
        } else {
            log.info("[拼团信息已写入数据库] openid = {}", groupDtl.getOpenid());
        }

    }

}
