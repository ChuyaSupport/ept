package com.ept.powersupport.service.redis;

import com.ept.powersupport.entity.*;
import com.ept.powersupport.repository.UserRepository;
import com.ept.powersupport.util.DBUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Redis初始化
 */
@Component
@Slf4j
public class RedisInit implements CommandLineRunner {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private DBUtil dbUtil;

    @Override
    public void run(String... args) throws SchedulerException, ParseException {
        HashOperations hashOperations = redisTemplate.opsForHash();
        SqlSession session = dbUtil.getSqlSession();
        UserRepository userRepository = session.getMapper(UserRepository.class);

        List<Business> businessList = new ArrayList<>();
        try {
            businessList = userRepository.loadAllBusinessInfo();
            session.commit();
        }catch (Exception e) {
            log.error("[Redis::商家列表初始化失败]");
        }
        for(int i=0; i<businessList.size(); ++i) {
            hashOperations.put("t_business", String.valueOf(businessList.get(i).getBusiness_id()), businessList.get(i));
        }

        List<Commodity> commodityList = new ArrayList<>();
        try {
            commodityList = userRepository.loadAllCommodityInfo();
            session.commit();
        }catch (Exception e) {
            log.error("[Redis::商品列表初始化失败]");
        }
        for(int i=0; i<commodityList.size(); ++i) {
            hashOperations.put("t_commodity", String.valueOf(commodityList.get(i).getCom_id()), commodityList.get(i));
        }

        List<User> userList = new ArrayList<>();
        try {
            userList = userRepository.loadAllUserInfo();
            session.commit();
        }catch (Exception e) {
            log.error("[Redis::用户列表初始化失败]");
        }
        for(int i=0; i<userList.size(); ++i) {
            hashOperations.put("t_user", userList.get(i).getOpenid(), userList.get(i));
        }

        List<Follow> followList = new ArrayList<>();
        try {
            followList = userRepository.loadAllFollowInfo();
            session.commit();
        }catch (Exception e) {
            log.error("[Redis::关注列表初始化失败]");
        }
        for(int i=0; i<followList.size(); ++i) {
            hashOperations.put("t_follow", followList.get(i).getFollow_id(), followList.get(i));
        }

        List<ComImg> imgList = new ArrayList<>();
        try {
            imgList = userRepository.loadAllComImg();
            session.commit();
        }catch (Exception e) {
            log.error("[Redis::图片列表初始化失败]");
        }
        for(int i=0; i<imgList.size(); ++i) {
            hashOperations.put("t_com_img", String.valueOf(imgList.get(i).getImg_id()), imgList.get(i));
        }

        List<Group> groupLists = new ArrayList<>();
        try {
            groupLists = userRepository.loadAllGroupList();
            session.commit();
        }catch (Exception e) {
            log.error("[Redis::拼团列表初始化失败]");
        }
        for(int i=0; i<groupLists.size(); ++i) {
            hashOperations.put("t_group_list", String.valueOf(groupLists.get(i).getGroup_id()), groupLists.get(i));
        }

        List<GroupDtl> groupDtls = new ArrayList<>();
        try {
            groupDtls = userRepository.loadAllGroupDtl();
            session.commit();
        }catch (Exception e) {
            log.error("[Redis::拼团详情表初始化失败]");
        }
        for(int i=0; i<groupDtls.size(); ++i) {
            hashOperations.put("t_group_dtl", String.valueOf(groupDtls.get(i).getJoin_id()), groupDtls.get(i));
        }

        List<Comment> commentLists = new ArrayList<>();
        try {
            commentLists = userRepository.loadAllCommentList();
            session.commit();
        }catch (Exception e) {
            log.error("[Redis::评论列表初始化失败]");
        }
        for(int i=0; i<commentLists.size(); ++i) {
            hashOperations.put("t_comment_list", String.valueOf(commentLists.get(i).getComment_id()), commentLists.get(i));
        }

        List<UserAddr> userAddrs = new ArrayList<>();
        try {
            userAddrs = userRepository.loadAllUserAddrInfo();
            session.commit();
        }catch (Exception e) {
            log.error("[Redis::用户地址表初始化失败]");
        }
        for(int i=0; i<userAddrs.size(); ++i) {
            hashOperations.put("t_user_addr", String.valueOf(userAddrs.get(i).getAddr_id()), userAddrs.get(i));
        }

        List<Coupon> coupons = new ArrayList<>();
        try {
            coupons = userRepository.loadAllCouponInfo();
            session.commit();
        }catch (Exception e) {
            e.printStackTrace();
            log.error("[Redis::用户券表初始化失败]");
        }
        for(int i=0; i<coupons.size(); ++i) {
            hashOperations.put("t_coupon", coupons.get(i).getCoupon_id(), coupons.get(i));
        }

        List<Order> orders = new ArrayList<>();
        try {
            orders = userRepository.loadAllOrderInfo();
            session.commit();
        }catch (Exception e) {
            e.printStackTrace();
            log.error("[Redis::订单表初始化失败]");
        }
        for(int i=0; i<orders.size(); ++i) {
            hashOperations.put("t_order", orders.get(i).getOrder_id(), orders.get(i));
        }
        log.info("[Redis::初始化完成]");
    }
}
