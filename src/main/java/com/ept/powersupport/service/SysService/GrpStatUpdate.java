package com.ept.powersupport.service.SysService;

import com.ept.powersupport.entity.Group;
import com.ept.powersupport.entity.GroupDtl;
import com.ept.powersupport.entity.Order;
import com.ept.powersupport.repository.UserRepository;
import com.ept.powersupport.util.DBUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GrpStatUpdate {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DBUtil dbUtil;

    public void update() {
        HashOperations hashOperations = redisTemplate.opsForHash();
        Map allGrp = hashOperations.entries("t_group_list");
        Map allGrpDtl = hashOperations.entries("t_group_dtl");
        SqlSession session = dbUtil.getSqlSession();
        UserRepository userRepository = session.getMapper(UserRepository.class);

        for(Object obj : allGrp.values()) {
            Group group = (Group) obj;
            if(group.getStatus() == 0 && group.getEnd_time().getTime() < System.currentTimeMillis()) {
                group.setStatus(2);
                hashOperations.put("t_group_list", group.getGroup_id(), obj);
                try {
                    userRepository.updateTGroupListStatus2((Group) obj);
                    session.commit();
                }catch (Exception e) {
                    log.error("[拼团表状态更新失败] group_id = {}", group.getGroup_id());
                }

                for(Object obj1 : allGrpDtl.values()) {
                    GroupDtl groupDtl = (GroupDtl) obj1;
                    if(groupDtl.getGroup_id().equals(group.getGroup_id())) {
                        groupDtl.setStatus(2);
                        hashOperations.put("t_group_dtl", groupDtl.getJoin_id(), obj1);
                        try {
                            userRepository.updateTGroupDtlStatus2((GroupDtl) obj1);
                            session.commit();
                        }catch (Exception e) {
                            log.error("[拼团表状态更新失败] join_id = {}", groupDtl.getGroup_id());
                        }
                    }
                }
            }
        }

        Map allOrder;
        List InvalidOrderList = new ArrayList();
        ValueOperations valueOperations = redisTemplate.opsForValue();
        allOrder = hashOperations.entries("t_order");
        if (allOrder != null) {
            for(Object object : allOrder.values()) {
                Order order = (Order) object;
                if (order.getPaid().equals("0")) {
                    if(valueOperations.get(order.getOrder_id()) == null) {
                        InvalidOrderList.add(object);
                    }
                }
            }
        }



        //删除数据库无效订单
        if (!InvalidOrderList.isEmpty()) {
            try {
                for(int i=0; i<InvalidOrderList.size(); ++i) {
                    //删除过期缓存
                    hashOperations.delete("t_group_dtl", ((Order) InvalidOrderList.get(i)).getJoin_id());
                    hashOperations.delete("t_order", ((Order) InvalidOrderList.get(i)).getOrder_id());

                    //数据库
                    userRepository.UnpaidDtlClean(((Order) InvalidOrderList.get(i)).getJoin_id());
                    userRepository.UnpaidOrderClean(((Order) InvalidOrderList.get(i)).getOrder_id());
                    session.commit();
                }
            }catch (Exception e) {
                e.printStackTrace();
                log.error("[数据库 :: 清理过期订单失败]");
            }
        }
        log.info("[Scheduled Tasks :: 数据库计划任务执行完毕]");
    }
}
