package com.ept.powersupport.service.user.Impl;

import com.ept.powersupport.entity.*;
import com.ept.powersupport.repository.UserRepository;
import com.ept.powersupport.resObj.*;
import com.ept.powersupport.service.SysService.GrpStatUpdate;
import com.ept.powersupport.service.scheduledTasks.PayIntimeTskMgr;
import com.ept.powersupport.service.user.*;
import com.ept.powersupport.util.DBUtil;
import com.ept.powersupport.util.DesUtil;
import com.ept.powersupport.util.DistanceCalc;
import com.ept.powersupport.util.ElectionUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.ibatis.session.SqlSession;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private DBUtil dbUtil;

    @Autowired
    private User user;

    @Autowired
    private Business business;

    @Autowired
    private DistanceCalc distanceCalc;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ShopDtl shopDtl;

    @Autowired
    private CommodityDtl commodityDtl;

    @Autowired
    private Commodity commodity;

    @Autowired
    private Group group;

    @Autowired
    private GroupDtl groupDtl;

    @Autowired
    private Follow follow;

    @Autowired
    private ElectionUtil electionUtil;

    @Autowired
    private ResOrderContent orderContent;

    @Autowired
    private ResAddrInfo resAddrInfo;

    @Autowired
    private Order order;

    @Value("${Pay.Timeout}")
    private String timeout;

    /**
     * ????????????
     *
     * @param openid
     * @return
     */
    @Override
    public User getUserByopenid(String openid) {

        HashOperations hashOperations = redisTemplate.opsForHash();
        user = (User) hashOperations.get("t_user", openid);
        return user;
    }

    /**
     * ??????3km??????????????????
     *
     * @param user
     * @return
     */
    @Override
    public List<Business> getBusinessInfo(User user) {

        //??????????????????
        BigDecimal SCOPE = new BigDecimal(0.03);
        List<Business> list = new ArrayList<>();

        //??????????????????
        HashOperations hashOperations = redisTemplate.opsForHash();
        Map map = hashOperations.entries("t_business");

        for (Object value : map.values()) {
            Business business = (Business) value;

            if (business.getBusiness_longitude() == null || business.getBusiness_latitude() == null) {
                continue;
            }

            if ((business.getBusiness_longitude().subtract(user.getUser_longitude())).abs().subtract(SCOPE).doubleValue() <= 0
                    && (business.getBusiness_latitude().subtract(user.getUser_latitude())).abs().subtract(SCOPE).doubleValue() <= 0
                    && business.getStatus() == 1) {
                list.add((Business) value);
            }
        }

        if (list.isEmpty()) {
            log.info("[????????????????????????]");
        } else {
            log.info("[?????????????????????]");
        }

        return list;
    }

    /**
     * ???????????????????????????
     *
     * @param list
     * @return
     */
    @Override
    public List<ResBusinessInfo> get3kmBusiness(List list, User user) {
        DecimalFormat df = new DecimalFormat("######0.00");
        List<ResBusinessInfo> reslist = new ArrayList<ResBusinessInfo>();
        Iterator itreator = list.iterator();
        while (itreator.hasNext()) {
            business = (Business) itreator.next();
            Double distance = distanceCalc.calcuateDistance(business.getBusiness_longitude(),
                    business.getBusiness_latitude(), user.getUser_longitude(), user.getUser_latitude()).doubleValue();
            if (distance <= 0.03) {
                ResBusinessInfo resBusinessInfo = new ResBusinessInfo();
                resBusinessInfo.setBusiness_id(String.valueOf(business.getBusiness_id()));
                resBusinessInfo.setShop_name(business.getShop_name());
                resBusinessInfo.setScore(business.getScore());
                resBusinessInfo.setMonthly_sales(String.valueOf(business.getMonthly_sales()));
                resBusinessInfo.setStart_delivery_price(business.getStart_delivery_price().toString());
                resBusinessInfo.setDelivery_price(business.getDelivery_price().toString());
                resBusinessInfo.setDelivery_distance(String.valueOf(df.format(distance * 111)));   //???????????????????????????  ????????????
                resBusinessInfo.setBusiness_logo(business.getBusiness_logo());
                resBusinessInfo.setDiscount_info(business.getDiscount_info());
                resBusinessInfo.setDelivery_time(String.valueOf(Integer.valueOf((int) (distance * 1850))));
                reslist.add(resBusinessInfo);
            }
        }

        return reslist;
    }

    /**
     * ????????????????????????
     *
     * @param resList
     * @param key_word
     * @return
     */
    @Override
    public List<ResBusinessInfo> getKeywordBusiness(List resList, String key_word) {
        List retList = new ArrayList();
        Iterator resListIterator = null;
        try {
            resListIterator = resList.iterator();
        } catch (Exception e) {

        }
        ResBusinessInfo resBusinessInfo;
        while (resListIterator.hasNext()) {
            resBusinessInfo = (ResBusinessInfo) resListIterator.next();
            if (resBusinessInfo.getShop_name().indexOf(key_word) != -1) {
                retList.add(resBusinessInfo);
            }
        }
        return retList;
    }


    /**
     * ??????redis????????????
     *
     * @param user
     */
    @Override
    public void redisUserInfoUpdate(User user) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        hashOperations.put("t_user", user.getOpenid(), user);
    }

    /**
     * ?????????????????????
     * @param business_id
     * @param user
     * @return
     */
    @Override
    public ShopDtl getShopDtl(String business_id, User user) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        business = (Business) hashOperations.get("t_business", business_id);

        if (business == null) {
            log.error("[???????????????] business_id = {}", business_id);
            return null;
        }


        List list = new ArrayList();

        //?????????????????????
        shopDtl.setShop_name(business.getShop_name());

        //??????????????????
        Double distance = distanceCalc.calcuateDistance(business.getBusiness_longitude(),
                business.getBusiness_latitude(), user.getUser_longitude(), user.getUser_latitude()).doubleValue();
        shopDtl.setDelivery_time(String.valueOf(Integer.valueOf((int) (distance * 1850))));

        shopDtl.setMonthly_sales(String.valueOf(business.getMonthly_sales()));
        shopDtl.setDiscount_info(business.getDiscount_info());
        shopDtl.setBusiness_logo(business.getBusiness_logo());
        shopDtl.setBusiness_notice(business.getBusiness_notice());
        shopDtl.setBusiness_backdrop(business.getBusiness_backdrop());
        shopDtl.setFollow("false");

        Map followMap = hashOperations.entries("t_follow");

        for (Object obj : followMap.values()) {
            Follow follow = (Follow) obj;
            if (follow.getOpenid().equals(user.getOpenid()) && follow.getBusiness_id().equals(business_id)) {
                shopDtl.setFollow("true");
                break;
            }
        }

        Map map = hashOperations.entries("t_commodity");
        List com_idList = new ArrayList();

        for (Object obj : map.values()) {
            Commodity commodity = (Commodity) obj;
            if (String.valueOf(commodity.getBusiness_id()).equals(business_id)) {
                ResCommodity resCommodity = new ResCommodity();
                com_idList.add(commodity.getCom_id());
                resCommodity.setCom_id(String.valueOf(commodity.getCom_id()));
                resCommodity.setCom_name(commodity.getCom_name());
                resCommodity.setCom_des(commodity.getCom_des());
                resCommodity.setCom_img(commodity.getCom_img());
                resCommodity.setCom_price(commodity.getCom_price());
                resCommodity.setCom_monthly_sales(String.valueOf(commodity.getCom_monthly_sales()));
                resCommodity.setComments_pct(commodity.getComments_pct());
                resCommodity.setCom_type(commodity.getCom_type());
                list.add(resCommodity);
            }
        }

        shopDtl.setCommodityList(list);

        int grpNow = 0;
        //??????????????????
        Map allGrp = hashOperations.entries("t_group_list");

        //?????????????????????
        List selectedGrp = new ArrayList();
        for(Object obj : allGrp.values()) {
            if(com_idList.contains(((Group) obj).getCom_id())) {
                selectedGrp.add(((Group) obj).getGroup_id());
            }
        }

        if(selectedGrp.size() == 0) {
            shopDtl.setGrouping_now("0");
        }else {
            //??????????????????
            Map allGrpDtl = hashOperations.entries("t_group_dtl");

            if(allGrpDtl == null) {
                shopDtl.setGrouping_now("0");
            } else {
                for(Object dtlObj : allGrpDtl.values()) {
                    if(selectedGrp.contains(((GroupDtl) dtlObj).getGroup_id()) && ((GroupDtl) dtlObj).getStatus() == 0) {
                        grpNow++;
                    }
                }
                shopDtl.setGrouping_now(String.valueOf(grpNow));
            }
        }


        //?????????????????????????????????null??????
        if (list == null) {
            list.add("null");
        }

        return shopDtl;
    }

    /**
     * ?????????????????????
     *
     * @param com_id
     * @return
     */
    @Override
    public CommodityDtl getCommodityDtl(String com_id, String eptcode) {

        HashOperations hashOperations = redisTemplate.opsForHash();
        List comImgList = new ArrayList();

        //??????????????????
        String business_id = String.valueOf(((Commodity) hashOperations.get("t_commodity", com_id)).getBusiness_id());
        //??????????????????
        business = (Business) hashOperations.get("t_business", business_id);


        //????????????
        user = (User) hashOperations.get("t_user", DesUtil.decrypt(eptcode));

        //??????????????????
        Map imgMap = hashOperations.entries("t_com_img");

        //??????????????????
        for (Object obj : imgMap.values()) {
            ComImg comImg = (ComImg) obj;
            if (String.valueOf(comImg.getCom_id()).equals(com_id)) {
                comImgList.add(comImg.getImg_url());
            }
        }


        commodityDtl.setComImgList(comImgList);
        commodityDtl.setCom_id(com_id);
        commodity = (Commodity) hashOperations.get("t_commodity", com_id);
        commodityDtl.setCom_price(commodity.getCom_price());
        commodityDtl.setFinal_price(new BigDecimal(commodity.getCom_price()).subtract(new BigDecimal(commodity.getDiscount_price())).toString());
        commodityDtl.setDaily_group("N/A");
        commodityDtl.setCom_name(commodity.getCom_name());
        commodityDtl.setCom_des(commodity.getCom_des());
        commodityDtl.setDiscount_str("*****?????????????????????*****");


        //????????????????????????
        List cmtList = hashOperations.values("t_comment_list");

        List commentList = new ArrayList();
        for (int i = 0; i < cmtList.size(); ++i) {
            Comment comment = (Comment) cmtList.get(i);
            if (String.valueOf(comment.getCom_id()).equals(com_id)) {
                ResComment resComment = new ResComment();
                resComment.setUser_profile(((User) hashOperations.get("t_user", comment.getOpenid())).getUser_profile());
                resComment.setUser_nickname(((User) hashOperations.get("t_user", comment.getOpenid())).getUser_name());
                resComment.setComment_time(String.valueOf(comment.getComment_time()).substring(0, 19));
                resComment.setContent(comment.getContent());
                commentList.add(resComment);
            }
        }

        commodityDtl.setComment_list(commentList);
        business = (Business) hashOperations.get("t_business", business_id);
        commodityDtl.setShop_name(business.getShop_name());

        //??????????????????
        Double distance = distanceCalc.calcuateDistance(business.getBusiness_longitude(),
                business.getBusiness_latitude(), user.getUser_longitude(), user.getUser_latitude()).doubleValue();
        commodityDtl.setDelivery_time(String.valueOf(Integer.valueOf((int) (distance * 1850))));
        commodityDtl.setBusiness_logo(business.getBusiness_logo());
        commodityDtl.setMonthly_sales(String.valueOf(business.getMonthly_sales()));
        commodityDtl.setDiscount_info(business.getDiscount_info());
        commodityDtl.setBusiness_notice(business.getBusiness_notice());
        log.info("[?????????????????????] com_id = {}", com_id);
        return commodityDtl;
    }

//    /**
//     * ???????????????????????????
//     *
//     * @param openid
//     * @param group_id
//     * @return
//     */
//    @Override
//    public String JoinGrp(String openid, String group_id) throws SchedulerException, ParseException {
//
//        int inGrpNum = 0;
//        List joinIdList = new ArrayList();
//
//        //???????????????????????????????????????
//        HashOperations hashOperations = redisTemplate.opsForHash();
//        group = (Group) hashOperations.get("t_group_list", group_id);
//
//        //?????????????????????
//        Map grpDtlMap = hashOperations.entries("t_group_dtl");
//
//        //?????????????????????????????????????????????
//        int com_id = group.getCom_id();
//        Map groupMap = hashOperations.entries("t_group_list");
//
//        for (Object obj : groupMap.values()) {
//            Group group1 = (Group) obj;
//            if (group1.getCom_id() == com_id && group1.getStatus() == 0) {
//                for (Object obj1 : grpDtlMap.values()) {
//                    GroupDtl groupDtl1 = (GroupDtl) obj1;
//                    if (groupDtl1.getGroup_id().equals(groupDtl1.getGroup_id()) && groupDtl1.getOpenid().equals(openid) && groupDtl1.getStatus() == 0) {
//                        log.error("[??????????????????????????????] openid = {}", openid);
//                        return "0";
//                    }
//                }
//            }
//        }
//
//        //????????????????????????
//        for (Object obj : grpDtlMap.values()) {
//            groupDtl = (GroupDtl) obj;
//            if (groupDtl.getGroup_id().equals(group_id) && groupDtl.getStatus() == 0) {
//                joinIdList.add(((GroupDtl) obj).getJoin_id());
//                inGrpNum++;
//            }
//        }
//
//        //??????????????????
//        commodity = (Commodity) hashOperations.get("t_commodity", String.valueOf(group.getCom_id()));
//
//        //???????????????????????????
//        if (inGrpNum < commodity.getGrp_pchs_num()
//                && group.getStart_time().getTime() / 1000 <= System.currentTimeMillis() / 1000
//                && group.getEnd_time().getTime() / 1000 > System.currentTimeMillis() / 1000
//                && group.getStatus() == 0) {
//
//            String join_id = String.valueOf(System.currentTimeMillis()) + commodity.getCom_id();
//
//            //????????????
//            groupDtl.setJoin_id(join_id);
//            groupDtl.setGroup_id(group_id);
//            groupDtl.setOpenid(openid);
//
//            //???????????????????????????
//            Thread thread = new AddGrpDtlThread(groupDtl);
//            thread.start();
//
//            //?????????????????????
//            hashOperations.put("t_group_dtl", groupDtl.getJoin_id(), groupDtl);
//            PayIntimeTskMgr payIntimeTskMgr = new PayIntimeTskMgr(join_id, timeout);
//            payIntimeTskMgr.startTsk();
//
//            return groupDtl.getJoin_id();
//        } else {
//            log.error("[??????????????????] openid = {}", openid);
//            return "0";
//        }
//    }

    /**
     * ????????????
     *
     * @param openid
     * @param group_id
     * @return
     */
    @Override
    public boolean QuitGrp(String openid, String group_id) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        String join_id = null;
        String order_id;
        Coupon coupon = null;
        BigDecimal resAmount = new BigDecimal(0);   //????????????
        group = (Group) hashOperations.get("t_group_list", group_id);
        int status;
        int option = 0;

        //?????????????????????????????????????????????
        if (group.getStatus() != 0) {
            log.error("[??????????????????] openid = {}", openid);
            return false;
        }

        Map map = hashOperations.entries("t_group_dtl");

        for (Object obj : map.values()) {
            groupDtl = (GroupDtl) obj;
            if (groupDtl.getGroup_id().equals(group_id) && groupDtl.getOpenid().equals(openid)) {
                join_id = groupDtl.getJoin_id();
                hashOperations.delete("t_group_dtl", join_id);
                option = 1;
                break;
            }
        }

        if (option == 0) {
            log.error("[?????????????????????] openid = {}", openid);
            return false;
        }

        Map allOrderMap = hashOperations.entries("t_order");
        for(Object ord : allOrderMap.values()) {
            if(((Order) ord).getJoin_id().equals(join_id)) {
                order = (Order) ord;
                order_id = ((Order) ord).getOrder_id();
                hashOperations.delete("t_order", order_id);
                break;
            }
        }

        //???????????????
        if(!order.getCoupon_id().equals("-1")) {
            coupon = (Coupon) hashOperations.get("t_coupon", order.getCoupon_id());
            coupon.setStatus("0");
            hashOperations.put("t_coupon", coupon.getCoupon_id(), coupon);
            resAmount = coupon.getValue().add(order.getPrice());
        }else {
            resAmount = order.getPrice();
        }
        user = (User) hashOperations.get("t_user", openid);
        user.setBalance(user.getBalance().add(resAmount));
        hashOperations.put("t_user", openid, user);

        SqlSession session = dbUtil.getSqlSession();
        UserRepository userRepository = session.getMapper(UserRepository.class);

        //???????????????
        try {
            status = userRepository.QuitGrp(openid, group_id);
            if(status != -1) {
                status = userRepository.deleteOrder(order);
                if(status != -1) {
                    status = userRepository.updateUserBalance(user);
                    if(status != -1 && coupon != null) {
                        status = userRepository.setCouponStat0(coupon);
                    }
                }


            }
        } catch (Exception e) {
            log.error("[??????????????????] openid = {}", openid);
            return false;
        }

        if (status != -1) {
            log.info("[??????????????????] openid = {}", openid);
            session.commit();
            return true;
        }

        log.error("[??????????????????] openid = {}", openid);
        return false;
    }

    /**
     * ??????????????????
     *
     * @return
     */
    @Override
    public List getGrpInfo(String openid, String com_id) {

        List grpList = new ArrayList();

        HashOperations hashOperations = redisTemplate.opsForHash();
        //??????????????????
        Map grpMap = hashOperations.entries("t_group_list");

        //?????????????????????
        Map grpDtlMap = hashOperations.entries("t_group_dtl");

        String inGrpAddr = null;

        //?????????????????????
        for (Object obj : grpMap.values()) {
            Group group = (Group) obj;

            if (String.valueOf(group.getCom_id()).equals(com_id)
                    && ((Group) obj).getEnd_time().getTime() - System.currentTimeMillis() > 0
                    && group.getStatus() != 2) {

                ResGroupInfo resGroupInfo = new ResGroupInfo();
                List grpUserList = new ArrayList();

                resGroupInfo.setGroup_id(String.valueOf(((Group) obj).getGroup_id()));
                resGroupInfo.setStart_time(String.valueOf(((Group) obj).getStart_time()).substring(0, 19));
                resGroupInfo.setEnd_time(String.valueOf(((Group) obj).getEnd_time()).substring(0, 19));

                resGroupInfo.setToStartTime(String.valueOf(((Group) obj).getStart_time().getTime() - System.currentTimeMillis()));
                resGroupInfo.setAvailable_time(String.valueOf(((Group) obj).getEnd_time().getTime() - System.currentTimeMillis()));

                //????????????????????????
                int available_num = ((Commodity) hashOperations.get("t_commodity", com_id)).getGrp_pchs_num();

                //????????????????????????
                for (Object object : grpDtlMap.values()) {
                    GroupDtl dtl = (GroupDtl) object;

                    if (String.valueOf(dtl.getGroup_id()).equals(resGroupInfo.getGroup_id())) {
                        grpUserList.add(((User) hashOperations.get("t_user", dtl.getOpenid())).getUser_profile());

                        if (dtl.getOpenid().equals(openid)) {
                            inGrpAddr = dtl.getGroup_id();
                        }

                        available_num--;
                    }
                }

                //????????????????????????
                for (int i = 0; i < available_num; ++i) {
                    grpUserList.add("http://pics.zhcy.site/avasit2.png");
                }

                resGroupInfo.setUser_list(grpUserList);

                //????????????????????????
                resGroupInfo.setAvailable_num(String.valueOf(available_num));

                if (resGroupInfo.getGroup_id().equals(inGrpAddr)) {
                    resGroupInfo.setIsInGrp("true");
                } else {
                    resGroupInfo.setIsInGrp("false");
                }
                grpList.add(resGroupInfo);
            }
        }

        return grpList;
    }

    /**
     * ??????????????????
     *
     * @param business_id
     * @param openid
     * @return
     */
    @Override
    public boolean checkIsFollow(String business_id, String openid) {
        log.info("[??????????????????] business_id = {}, openid = {}", business_id, openid);

        HashOperations hashOperations = redisTemplate.opsForHash();
        Map map = hashOperations.entries("t_follow");

        for (Object obj : map.values()) {
            Follow follow = (Follow) obj;
            if (follow.getOpenid().equals(openid) && follow.getBusiness_id().equals(business_id)) {
                return true;
            }
        }

        return false;
    }

    /**
     * ????????????
     *
     * @param business_id
     * @param openid
     * @return
     */
    @Override
    public boolean addFollow(String business_id, String openid) {
        String follow_id = business_id + System.currentTimeMillis() + (int) (Math.random() * 100);
        HashOperations hashOperations = redisTemplate.opsForHash();

        Map map = hashOperations.entries("t_follow");

        for (Object obj : map.values()) {
            Follow fl = (Follow) obj;
            if (fl.getOpenid().equals(openid) && fl.getBusiness_id().equals(business_id)) {
                log.error("[??????????????????] business_id = {}, openid = {}", business_id, openid);
                return false;
            }
        }

        follow.setFollow_id(follow_id);
        follow.setOpenid(openid);
        follow.setBusiness_id(business_id);

        try {
            hashOperations.put("t_follow", follow_id, follow);
            Thread thread = new AddFollowThread(follow);
            thread.start();

        } catch (Exception e) {
            log.error("[??????????????????] business_id = {}, openid = {}", business_id, openid);
        }

        log.info("[??????????????????] business_id = {}, openid = {}", business_id, openid);
        return true;
    }

    /**
     * ????????????
     *
     * @param business_id
     * @param openid
     * @return
     */
    @Override
    public boolean removeFollow(String business_id, String openid) {

        HashOperations hashOperations = redisTemplate.opsForHash();
        Map map = hashOperations.entries("t_follow");

        for (Object obj : map.values()) {
            Follow fl = (Follow) obj;
            if (fl.getOpenid().equals(openid) && fl.getBusiness_id().equals(business_id)) {
                hashOperations.delete("t_follow", fl.getFollow_id());
                Thread thread = new RemoveFollowThread(fl);
                thread.start();
                log.info("[??????????????????] business_id = {}, openid = {}", business_id, openid);
                return true;
            }
        }

        log.error("[??????????????????] business_id = {}, openid = {}", business_id, openid);
        return false;
    }

    /**
     * ??????????????????
     *
     * @param userAddr
     * @return
     */
    @Override
    public boolean newAddr(UserAddr userAddr) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        String updateUserAddrId = null;

        Map allAddrMap = hashOperations.entries("t_user_addr");
        for(Object object : allAddrMap.values()) {
            UserAddr userAddr1 = (UserAddr) object;
            if (userAddr1.getOpenid().equals(userAddr.getOpenid()) && userAddr1.getRecently_used().equals("1")) {
                updateUserAddrId = userAddr1.getAddr_id();
                userAddr1.setRecently_used("0");
                hashOperations.put("t_user_addr", updateUserAddrId, userAddr1);
            }
        }


        try {
            userAddr.setAddr_id(String.valueOf(((User) hashOperations.get("t_user", userAddr.getOpenid())).getUser_id())
                    + System.currentTimeMillis());
            userAddr.setRecently_used("1");

            hashOperations.put("t_user_addr", userAddr.getAddr_id(), userAddr);

            Thread thread = new NewAddrThread(userAddr, updateUserAddrId);
            thread.start();
        } catch (Exception e) {
            log.error("[????????????????????????] addr_id = {}", userAddr.getAddr_id());
        }

        return true;
    }

    /**
     * ??????????????????
     *
     * @param openid
     * @return
     */
    @Override
    public List lsAddr(String openid, String busienss_id) {
        List resList = new ArrayList();

        HashOperations hashOperations = redisTemplate.opsForHash();

        //????????????????????????
        Map alladdr = hashOperations.entries("t_user_addr");
        business = (Business) hashOperations.get("t_business", busienss_id);

        //???????????????addr
        for (Object obj : alladdr.values()) {
            UserAddr userAddr = (UserAddr) obj;
            if (userAddr.getOpenid().equals(openid)) {
                ResAddrInfo resAddrInfo = new ResAddrInfo();
                resAddrInfo.setAddr_id(userAddr.getAddr_id());
                resAddrInfo.setName(userAddr.getName());
                if(userAddr.getGender().equals("F"))
                    resAddrInfo.setGender("??????");
                else
                    resAddrInfo.setGender("??????");
                resAddrInfo.setPhone(userAddr.getPhone());
                resAddrInfo.setAddr(userAddr.getAddr());

                if (distanceCalc.calcuateDistance(business.getBusiness_longitude(), business.getBusiness_latitude(),
                        BigDecimal.valueOf(Double.valueOf(userAddr.getUser_longitude())),
                        BigDecimal.valueOf(Double.valueOf(userAddr.getUser_latitude()))).doubleValue()
                        <= 0.03) {
                    resAddrInfo.setValidPresent("1");
                }else {
                    resAddrInfo.setValidPresent("0");
                }

                resList.add(resAddrInfo);

            }
        }

        log.info("[????????????????????????] openid = {}", openid);

        return resList;
    }

    /**
     * ??????????????????
     * @param openid
     * @param addr_id
     * @return
     */
    @Override
    public boolean delAddr(String openid, String addr_id) {

        HashOperations hashOperations = redisTemplate.opsForHash();
        try {
            UserAddr userAddr = (UserAddr) hashOperations.get("t_user_addr", addr_id);

            if(userAddr == null) {
                log.error("[??????????????????????????????] addr_id = {}", addr_id);
                return false;
            }


            if (userAddr.getOpenid().equals(openid)) {
                hashOperations.delete("t_user_addr", addr_id);
                Thread thread = new DelAddrThread(addr_id);
                thread.start();
            }

        } catch (Exception e) {
            log.error("[??????????????????????????????] addr_id = {}", addr_id);
        }

        return true;
    }

    /**
     * ???????????????????????????
     * @param openid
     * @return
     */
    @Override
    public Object lsRctAddr(String openid, String business_id) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        Map allAddrMap = hashOperations.entries("t_user_addr");
        Business business = (Business) hashOperations.get("t_business", business_id);

        for(Object obj : allAddrMap.values()) {
            UserAddr userAddr = (UserAddr) obj;
            if(userAddr.getRecently_used().equals("1")) {
                resAddrInfo.setAddr_id(userAddr.getAddr_id());
                resAddrInfo.setName(userAddr.getName());
                if(userAddr.getGender().equals("F"))
                    resAddrInfo.setGender("??????");
                else
                    resAddrInfo.setGender("??????");
                resAddrInfo.setPhone(userAddr.getPhone());
                resAddrInfo.setAddr(userAddr.getAddr());

                if (distanceCalc.calcuateDistance(business.getBusiness_longitude(), business.getBusiness_latitude(),
                        BigDecimal.valueOf(Double.valueOf(userAddr.getUser_longitude())),
                        BigDecimal.valueOf(Double.valueOf(userAddr.getUser_latitude()))).doubleValue()
                        <= 0.03) {
                    resAddrInfo.setValidPresent("1");
                }else {
                    resAddrInfo.setValidPresent("0");
                }

                return resAddrInfo;
            }
        }
        return null;
    }

    /**
     * ?????????????????????
     * @param openid
     * @return
     */
    @Override
    public Object orderContent(String openid, String com_id) {

        HashOperations hashOperations = redisTemplate.opsForHash();
        commodity = (Commodity) hashOperations.get("t_commodity", com_id);
        if(commodity == null)
            return null;
        Business business = (Business) hashOperations.get("t_business", String.valueOf(commodity.getBusiness_id()));

        orderContent.setShop_name(business.getShop_name());
        orderContent.setCom_name(commodity.getCom_name());
        orderContent.setCom_price(commodity.getCom_price());
        orderContent.setCom_img(commodity.getCom_img());

        return orderContent;
    }

    /**
     * ?????????????????????
     * @param openid
     * @param com_id
     * @return
     */
    @Override
    public List<Coupon> getCoupon(String openid, String com_id) {
        List couponList = new ArrayList();
        HashOperations hashOperations = redisTemplate.opsForHash();
        Map allCoupons = null;
        String business_id = null;
        try {
            commodity = (Commodity) hashOperations.get("t_commodity", com_id);
            allCoupons = hashOperations.entries("t_coupon");
            if(commodity != null)
                business_id = String.valueOf(commodity.getBusiness_id());
            else
                business_id = null;
        }catch (Exception e) {
            e.printStackTrace();
            log.error("[???????????????????????????] openid = {}", openid);
        }

        if(allCoupons != null)
            for(Object obj : allCoupons.values()) {
                Coupon coupon = (Coupon) obj;
                if(coupon.getOpenid().equals(openid) && coupon.getBusiness_id().equals(business_id)) {
                    ResCoupon resCoupon = new ResCoupon();
                    resCoupon.setCoupon_id(coupon.getCoupon_id());
                    resCoupon.setValue(coupon.getValue().toString());
                    couponList.add(resCoupon);
                }
            }

        log.info("[????????????????????????] openid = {}", openid);

        return couponList;
    }

//    /**
//     * ????????????
//     * @param eptcode
//     * @param join_id
//     * @param coupon_id
//     * @param addr_id
//     * @param note
//     * @param tableware
//     * @return
//     */
//    @Override
//    public Object newOrder(String eptcode, String join_id, String coupon_id, String addr_id, String note, String tableware) {
//
//        int inGrpNum = 0;
//        List joinIdList = new ArrayList();
//        String group_id;
//
//        //???????????????????????????????????????
//        HashOperations hashOperations = redisTemplate.opsForHash();
//        group_id = ((GroupDtl) hashOperations.get("t_group_dtl", join_id)).getGroup_id();
//        group = (Group) hashOperations.get("t_group_list", group_id);
//        int com_id = ((Group) hashOperations.get("t_group_list", group_id)).getCom_id();
//        commodity = (Commodity) hashOperations.get("t_commodity", String.valueOf(com_id));
//
//        //?????????????????????
//        Map grpDtlMap = hashOperations.entries("t_group_dtl");
//
//        //????????????????????????
//        for (Object obj : grpDtlMap.values()) {
//            groupDtl = (GroupDtl) obj;
//            if (groupDtl.getGroup_id().equals(group_id) && groupDtl.getStatus() == 0) {
//                joinIdList.add(((GroupDtl) obj).getJoin_id());
//                inGrpNum++;
//            }
//        }
//
//        try {
//            String order_id = join_id.substring(4, 18) + String.valueOf(System.currentTimeMillis()).substring(9, 13);
//            order.setOrder_id(order_id);
//            order.setJoin_id(join_id);
//            order.setCoupon_id(coupon_id);
//            order.setAddr_id(addr_id);
//            order.setNote(note);
//            order.setTableware(tableware);
//            //?????????????????????
//            String jobName = "DEL" + join_id;
//            String TRIGGER_GROUP_NAME = "DEL" + join_id;
//            String JOB_GROUP_NAME = "DEL" + join_id;
//            DELScheduleTask delScheduleTask = new DELScheduleTask();
//            delScheduleTask.delTask(jobName, TRIGGER_GROUP_NAME, JOB_GROUP_NAME);
//
//
//            //????????????
//            hashOperations.put("t_order", order_id, order);
//
//            //?????????, ???????????????
//            Thread thread = new NewOrderThread(order);
//            thread.start();
//
//            //??????????????????
//            //????????????????????????
//            if (inGrpNum == commodity.getGrp_pchs_num()) {
//                log.info("[????????????, ????????????????????????] group_id = {}", group_id);
//                List groupMember = new ArrayList();
//                Map freeMap = electionUtil.elect(commodity.getGrp_pchs_num(), Integer.parseInt(group.getFree_num()));
//
//                //??????????????????
//                Group group = (Group) hashOperations.get("t_group_list", group_id);
//                group.setStatus(1);
//                hashOperations.put("t_group_list", group_id, group);
//
//                for (int i = 0; i < joinIdList.size(); ++i) {
//                    GroupDtl groupDtl = (GroupDtl) hashOperations.get("t_group_dtl", String.valueOf(joinIdList.get(i)));
//                    groupDtl.setStatus(1);
//                    groupDtl.setIs_free((Integer) freeMap.get(i));
//                    GroupDtl grpDtl = new GroupDtl();
//                    grpDtl.setJoin_id(groupDtl.getJoin_id());
//                    grpDtl.setGroup_id(group.getGroup_id());
//                    grpDtl.setOpenid(groupDtl.getOpenid());
//                    grpDtl.setStatus(groupDtl.getStatus());
//                    grpDtl.setIs_free(groupDtl.getIs_free());
//                    groupMember.add(grpDtl);
//                    hashOperations.put("t_group_dtl", groupDtl.getJoin_id(), groupDtl);
//                }
//
//                //???????????????????????????????????????
//
//
//
//
//                //?????????????????????
//                Thread thread1 = new GrpFinishedThread(joinIdList, group_id);
//                thread1.start();
//
//            }
//        }catch (Exception e) {
//            log.error("[??????????????????] join_id = {}", join_id);
//            return 0;
//        }
//        log.info("[??????????????? order_id = {}]", order.getOrder_id());
//
//        return order.getOrder_id();
//    }

    /**
     * ????????????
     * @param openid
     * @param group_id
     * @param coupon_id
     * @param addr_id
     * @param note
     * @param tableware
     * @return
     */
    @Override
    public Object joinGroup(String openid, String group_id, String coupon_id, String addr_id, String note, String tableware) {

        //????????????
        int inGrpNum = 0;
        List joinIdList = new ArrayList();
        String join_id;
        Map resultMap = new HashMap();

        //???????????????????????????????????????
        HashOperations hashOperations = redisTemplate.opsForHash();
        group = (Group) hashOperations.get("t_group_list", group_id);

        //?????????????????????
        Map grpDtlMap = hashOperations.entries("t_group_dtl");

        //?????????????????????????????????????????????
        int com_id = group.getCom_id();
        Map groupMap = hashOperations.entries("t_group_list");

        for (Object obj : groupMap.values()) {
            Group group1 = (Group) obj;
            if (group1.getCom_id() == com_id && group1.getStatus() == 0) {
                for (Object obj1 : grpDtlMap.values()) {
                    GroupDtl groupDtl1 = (GroupDtl) obj1;
                    if (groupDtl1.getGroup_id().equals(group1.getGroup_id()) && groupDtl1.getOpenid().equals(openid) && groupDtl1.getStatus() == 0) {
                        log.error("[??????????????????????????????] openid = {}", openid);
                        resultMap.put("status", "0");
                        return resultMap;
                    }
                }
            }
        }

        //????????????????????????
        for (Object obj : grpDtlMap.values()) {
            groupDtl = (GroupDtl) obj;
            if (groupDtl.getGroup_id().equals(group_id) && groupDtl.getStatus() == 0) {
                joinIdList.add(((GroupDtl) obj).getJoin_id());
                inGrpNum++;
            }
        }

        //??????????????????
        commodity = (Commodity) hashOperations.get("t_commodity", String.valueOf(group.getCom_id()));

        //???????????????????????????
        if (inGrpNum < commodity.getGrp_pchs_num()
                && group.getStart_time().getTime() / 1000 <= System.currentTimeMillis() / 1000
                && group.getEnd_time().getTime() / 1000 > System.currentTimeMillis() / 1000
                && group.getStatus() == 0) {

            //????????????
            join_id = String.valueOf(System.currentTimeMillis()) + commodity.getCom_id();
            groupDtl.setJoin_id(join_id);
            groupDtl.setGroup_id(group_id);
            groupDtl.setOpenid(openid);
            if (inGrpNum + 1 == commodity.getGrp_pchs_num()) {
                groupDtl.setStatus(1);

            } else {
                groupDtl.setStatus(0);
            }

            hashOperations.put("t_group_dtl", groupDtl.getJoin_id(), groupDtl);

            //???????????????????????????
            Thread thread = new AddGrpDtlThread(groupDtl);
            thread.start();

            //????????????????????????
            if (inGrpNum + 1 == commodity.getGrp_pchs_num()) {
                log.info("[????????????, ????????????????????????] group_id = {}", group_id);

                Map freeMap = electionUtil.elect(commodity.getGrp_pchs_num(), Integer.parseInt(group.getFree_num()));

                //??????????????????
                Group group = (Group) hashOperations.get("t_group_list", group_id);
                group.setStatus(1);
                hashOperations.put("t_group_list", group_id, group);

                for (int i = 0; i < joinIdList.size(); ++i) {
                    GroupDtl groupDtl = (GroupDtl) hashOperations.get("t_group_dtl", String.valueOf(joinIdList.get(i)));
                    groupDtl.setStatus(1);
                    groupDtl.setIs_free((Integer) freeMap.get(i));

                    //???????????????????????????????????????
                    if((Integer) freeMap.get(i) == 0) {
                        Coupon coupon = new Coupon();
                        coupon.setCoupon_id(System.currentTimeMillis() + join_id.substring(9, 13));
                        coupon.setOpenid(groupDtl.getOpenid());
                        coupon.setBusiness_id(String.valueOf(business.getBusiness_id()));
                        coupon.setValue(BigDecimal.valueOf(Double.valueOf(commodity.getDiscount_price())));
                        hashOperations.put("t_coupon", coupon.getCoupon_id(), coupon);

                        //????????????????????????????????????
                        Thread thread1 = new CouponHangOutThread(coupon);
                        thread1.start();

                    }
                    hashOperations.put("t_group_dtl", groupDtl.getJoin_id(), groupDtl);
                }

                //?????????????????????
                Thread thread1 = new GrpFinishedThread(joinIdList, group_id);
                thread1.start();

            }

            String order_id = join_id.substring(4, 18) + String.valueOf(System.currentTimeMillis()).substring(9, 13);
            order.setOrder_id(order_id);
            order.setJoin_id(join_id);
            order.setCoupon_id(coupon_id);
            order.setAddr_id(addr_id);
            order.setNote(note);
            order.setTableware(tableware);
            order.setPaid("0");
            order.setPrice(BigDecimal.valueOf(Double.valueOf(commodity.getCom_price())));
            //????????????
            hashOperations.put("t_order", order_id, order);

            //?????????, ???????????????
            Thread orderThread = new NewOrderThread(order);
            orderThread.start();

            //???????????????
            ValueOperations valueOperations = redisTemplate.opsForValue();
            valueOperations.set(order_id, order, Integer.parseInt(timeout), TimeUnit.MILLISECONDS);
//            //?????????????????????
//            try {
//                PayIntimeTskMgr payIntimeTskMgr = new PayIntimeTskMgr(join_id, timeout);
//                payIntimeTskMgr.startTsk();
//            }catch (Exception e) {
//                e.printStackTrace();
//                log.error("[Schedule Tasks :: ?????????????????????] openid = {}", openid);
//            }


            resultMap.put("status", "1");
            resultMap.put("order_id", order_id);
            resultMap.put("timeout", timeout);
            return resultMap;
        } else {
            resultMap.put("status", "0");
            return resultMap;
        }
    }

    /**
     * ??????
     * @param openid
     * @param order_id
     * @return
     */
    @Override
    public Object pay(String openid, String order_id) {
        Map resMap = new HashMap();
        String join_id;
        Order order;
        HashOperations hashOperations = redisTemplate.opsForHash();
        Coupon coupon = null;
        order = (Order) hashOperations.get("t_order", order_id);
        user = (User) hashOperations.get("t_user", openid);
        int status = 0;

        //??????????????????
        ValueOperations valueOperations = redisTemplate.opsForValue();
        if(valueOperations.get(order_id) == null) {
            join_id = order.getJoin_id();
            hashOperations.delete("t_group_dtl", join_id);
            hashOperations.delete("t_order", order_id);
            resMap.put("status", "0");

            //????????????????????????
            Thread thread = new UnPaidOrderClean(join_id, order_id);
            thread.start();
            log.error("[????????????] order_id = {}", order_id);
            return resMap;
        }

        //??????
        BigDecimal subPrice;    //????????????
        if (order.getCoupon_id().equals("-1")) {
            //??????????????????????????????
            subPrice = order.getPrice();
        } else {
            //???????????????
            coupon = (Coupon) hashOperations.get("t_coupon", order.getCoupon_id());
            if(coupon == null) {
                //??????????????????
                resMap.put("status", "-2");
                log.error("[??????(?????????)????????????] coupon_id = {}", order.getCoupon_id());
                return resMap;
            }
            //???????????????????????????
            //?????????????????????????????????
            if(coupon.getValue().subtract(order.getPrice()).doubleValue() >= 0) {
                subPrice = BigDecimal.valueOf(0);
            }else {
                subPrice = order.getPrice().subtract(coupon.getValue());
            }
        }

        //????????????????????????
        if(user.getBalance().subtract(subPrice).doubleValue() < 0) {
            //????????????
            resMap.put("status", -1);
            log.error("[????????????] openid = {}", openid);
            return resMap;
        }else {
            //????????????
            //??????
            user.setBalance(user.getBalance().subtract(subPrice));
            hashOperations.put("t_user", user.getOpenid(), user);

            //?????????
            SqlSession session = dbUtil.getSqlSession();
            UserRepository userRepository = session.getMapper(UserRepository.class);

            try {
                status = userRepository.cost(user);
            }catch (Exception e) {

                e.printStackTrace();
            }

            if (status != -1) {
                session.commit();
                log.info("[??????????????????] openid = {}???order_id = {}", openid, order_id);
            }
        }

        //?????????????????????
        order.setPaid("1");
        hashOperations.put("t_order", order_id, order);

        //????????????????????????
        Thread thread = new PaidStatusUpdateThread(order_id);
        thread.start();

        //?????????????????????
        if (!order.getCoupon_id().equals("-1")) {
            coupon.setStatus("1");

            hashOperations.put("t_coupon", coupon.getCoupon_id(), coupon);
            Thread couponStatthread = new CouponThread(coupon);
            couponStatthread.start();
        }

        redisTemplate.delete(order_id);

        resMap.put("status", "1");
        return resMap;
    }

    /**
     * ????????????
     * @param openid
     * @return
     */
    @Override
    public BigDecimal balance(String openid) {

        HashOperations hashOperations = redisTemplate.opsForHash();
        User user = (User) hashOperations.get("t_user", openid);
        return user.getBalance();
    }

    /**
     * ??????
     * @param openid
     * @param amount
     * @return
     */
    @Override
    public BigDecimal topup(String openid, BigDecimal amount) {

        SqlSession session = dbUtil.getSqlSession();
        UserRepository userRepository = session.getMapper(UserRepository.class);
        int status = 0;
        HashOperations hashOperations = redisTemplate.opsForHash();
        user = (User) hashOperations.get("t_user", openid);
        BigDecimal banalceNow = user.getBalance();
        user.setBalance(banalceNow.add(amount));

        try {
            status = userRepository.topUp(user);
        }catch (Exception e) {
            e.printStackTrace();
            log.error("[????????????] openid = {}", openid);
        }

        if(status != -1) {
            hashOperations.put("t_user", user.getOpenid(), user);
            session.commit();
            log.info("[????????????] openid = {}", openid);
        }

        return user.getBalance();
    }

    /**
     * ??????????????????
     * @param openid
     * @return
     */
    @Override
    public Object myOrder(String openid) {

        List myOrderlist = new ArrayList();
        List selectedGroupDtl = new ArrayList();

        HashOperations hashOperations = redisTemplate.opsForHash();

        //??????????????????????????????
        Map allGrpDtlMap = hashOperations.entries("t_group_dtl");



        //?????????????????????????????????
        if(allGrpDtlMap.isEmpty()) {
            myOrderlist.add("null");
            return myOrderlist;
        }else {
            for(Object obj : allGrpDtlMap.values()) {
                if(((GroupDtl) obj).getOpenid().equals(openid)) {
                    selectedGroupDtl.add(obj);
                }
            }

        }

        //??????????????????
        Map allOrder = hashOperations.entries("t_order");

        for(int i=0; i<selectedGroupDtl.size(); ++i) {
            ResMyOrder resMyOrder = new ResMyOrder();
            Group grp = (Group) hashOperations.get("t_group_list", ((GroupDtl) selectedGroupDtl.get(i)).getGroup_id());
            resMyOrder.setStatus(String.valueOf(grp.getStatus()));
            Commodity commodity = (Commodity) hashOperations.get("t_commodity", String.valueOf(grp.getCom_id()));
            resMyOrder.setCom_name(commodity.getCom_name());
            resMyOrder.setCom_price(commodity.getCom_price());
            resMyOrder.setCom_img(commodity.getCom_img());
            Business business = (Business) hashOperations.get("t_business", String.valueOf(commodity.getBusiness_id()));
            resMyOrder.setBusiness_logo(business.getBusiness_logo());
            resMyOrder.setShop_name(business.getShop_name());
            resMyOrder.setCom_id(String.valueOf(commodity.getCom_id()));
            for (Object obj : allOrder.values()) {
                if(((Order) obj).getJoin_id().equals(((GroupDtl) selectedGroupDtl.get(i)).getJoin_id())) {
                    if(((Order) obj).getCoupon_id().equals("-1")) {
                        resMyOrder.setCoupon(null);
                    }else {
                        resMyOrder.setCoupon((Coupon) hashOperations.get("t_coupon", ((Order) obj).getCoupon_id()));
                    }

                    if(((GroupDtl) selectedGroupDtl.get(i)).getIs_free() == 1) {
                        resMyOrder.setRealCost("0");
                    }else {
                        if(((Order) obj).getCoupon_id().equals("-1")) {
                            resMyOrder.setRealCost(((Order) obj).getPrice().toString());
                        }else {
                            resMyOrder.setRealCost(((Order) obj).getPrice().subtract(resMyOrder.getCoupon().getValue()).toString());
                        }
                    }
                    break;
                }
            }
            myOrderlist.add(resMyOrder);
        }

        if(myOrderlist.isEmpty()) {
            myOrderlist.add("null");
        }

        return myOrderlist;
    }

    /**
     * ???????????? ????????????
     * @param order_id
     * @return
     */
    @Override
    public Object payCancel(String order_id) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(order_id, "", 1, TimeUnit.MILLISECONDS);

        String url = "http://localhost/grpStatusUpdate";

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        try {
            //??????????????????
            okHttpClient.newCall(request).execute();
        }catch (Exception e)
        {
            log.error("[???????????????????????????????????????] order_id = {}", order_id);
            return false;
        }

        return true;
    }
}
