package com.ept.powersupport.service.user;


import com.ept.powersupport.entity.*;
import com.ept.powersupport.resObj.CommodityDtl;
import com.ept.powersupport.resObj.ResBusinessInfo;
import com.ept.powersupport.resObj.ResCommodity;
import com.ept.powersupport.resObj.ShopDtl;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

public interface UserService {
    User getUserByopenid(String openid);
    List<Business> getBusinessInfo(User user);
    List<ResBusinessInfo> get3kmBusiness(List list, User user);
    List<ResBusinessInfo> getKeywordBusiness(List list, String key_word);
    void redisUserInfoUpdate(User user);
    ShopDtl getShopDtl(String business_id, User user);
    CommodityDtl getCommodityDtl(String com_id, String eptcode);
//    String JoinGrp(String openid, String group_id) throws SchedulerException, ParseException;
    boolean QuitGrp(String openid, String group_id);
    List getGrpInfo(String openid, String com_id);
    boolean checkIsFollow(String business_id, String openid);
    boolean addFollow(String business_id, String openid);
    boolean removeFollow(String business_id, String openid);
    boolean newAddr(UserAddr userAddr);
    List lsAddr(String openid, String busienss_id);
    boolean delAddr(String opendid, String addr_id);
    Object lsRctAddr(String openid, String business_id);
    Object orderContent(String openid, String com_id);
    List<Coupon> getCoupon(String openid, String com_id);
//    Object newOrder(String eptcode, String join_id, String coupon_id, String addr_id, String note, String tableware);
    Object joinGroup(String openid, String group_id, String coupon_id, String addr_id, String note, String tableware) throws SchedulerException, ParseException;
    Object pay(String openid, String order_id);
    BigDecimal balance(String openid);
    BigDecimal topup(String openid, BigDecimal amount);
    Object myOrder(String openid);
    Object payCancel(String order_id);
}

