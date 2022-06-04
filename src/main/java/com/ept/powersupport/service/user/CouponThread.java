package com.ept.powersupport.service.user;

import com.ept.powersupport.entity.Coupon;
import com.ept.powersupport.repository.UserRepository;
import com.ept.powersupport.util.DBUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;

@Slf4j
public class CouponThread extends Thread{

    private Coupon coupon;

    public CouponThread(Coupon coupon) {
        this.coupon = coupon;
    }

    @Override
    public void run() {
        DBUtil dbUtil = new DBUtil();
        SqlSession session = dbUtil.getSqlSession();
        UserRepository userRepository = session.getMapper(UserRepository.class);
        int status = 0;

        try {
            status = userRepository.couponUpdate(coupon);
         }catch (Exception e) {
            log.error("[数据库 :: 优惠券状态更新失败] coupon_id = {}", coupon.getCoupon_id());
            e.printStackTrace();
        }

        if (status != -1) {
            session.commit();
            log.info("[数据库 :: 优惠券状态已更新] coupon_id = {}", coupon.getCoupon_id());
        }else {
            log.error("[数据库 :: 优惠券状态更新失败] coupon_id = {}", coupon.getCoupon_id());
        }
    }
}
