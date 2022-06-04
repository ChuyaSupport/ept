package com.ept.powersupport.repository;

import com.ept.powersupport.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserRepository {

    int save(User user);
    int update(User user);
    List<User> findByOpenid(String openid);
    int addFollow(Follow follow);
    int rmFollow(Follow follow);
    List<Business> loadAllBusinessInfo();
    List<Commodity> loadAllCommodityInfo();
    List<Follow> loadAllFollowInfo();
    List<User> loadAllUserInfo();
    User findSingleUserByOpenid(String openid);
    List<ComImg> loadAllComImg();
    List<Group> loadAllGroupList();
    List<GroupDtl> loadAllGroupDtl();
    List<Comment> loadAllCommentList();
    int addGrpDtl(GroupDtl groupDtl);
    int updateTGroupListStatus1(String group_id);
    int updateTGroupDtlStatus1(String join_id);
    int QuitGrp(@Param("openid") String openid, @Param("group_id") String group_id);
    int updateTGroupListStatus2(Group group);
    int updateTGroupDtlStatus2(GroupDtl groupDtl);
    int newAddr(UserAddr userAddr);
    int delAddr(String addr_id);
    List<UserAddr> loadAllUserAddrInfo();
    List<Coupon> loadAllCouponInfo();
    int newOrder(Order order);
    int updateTGrpDtlFree1(String join_id);
    int deleteOrder(Order order);
    int updateUserAddrStatus(String addr_id);
    List<Order> loadAllOrderInfo();
    int updatePaidStatus(String order_id);
    int UnpaidDtlClean(String join_id);
    int UnpaidOrderClean(String join_id);
    int topUp(User user);
    int cost(User user);
    int couponUpdate(Coupon coupon);
    int couponHangOut(Coupon coupon);
    int setCouponStat0(Coupon coupon);
    int updateUserBalance(User user);
}
