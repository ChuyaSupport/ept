package com.ept.powersupport.service.business.Impl;

import com.ept.powersupport.config.Constants;
import com.ept.powersupport.entity.*;
import com.ept.powersupport.repository.BusinessRepository;
import com.ept.powersupport.reqObj.BusInitInfo;
import com.ept.powersupport.reqObj.ComInfo;
import com.ept.powersupport.resObj.ResBusCommodity;
import com.ept.powersupport.resObj.ResBusGrpInfo;
import com.ept.powersupport.service.business.BusInfoUpdateThread;
import com.ept.powersupport.service.business.BusinessService;
import com.ept.powersupport.service.business.ComDelThread;
import com.ept.powersupport.service.business.GrpInfoUpdateThread;
import com.ept.powersupport.service.qiniu.QiniuService;
import com.ept.powersupport.util.DBUtil;
import com.ept.powersupport.util.DesUtil;
import com.ept.powersupport.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
public class BusinessServiceImpl implements BusinessService {

    @Autowired
    private DBUtil dbUtil;

    @Autowired
    private Business business;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private QiniuService qiniuService;

    @Autowired
    private ComImg comImg;

    @Autowired
    private Commodity commodity;

    private String QiniuURL = Constants.QINIU_URL;

    /**
     * 检查手机号是否注册
     * @param phone_number
     * @return
     */
    @Override
    public boolean checkIsExisted(String phone_number) {
        SqlSession session = dbUtil.getSqlSession();
        BusinessRepository businessRepository = session.getMapper(BusinessRepository.class);

        try {
            if(businessRepository.checkIsPhoneNumberExisted(phone_number) != null) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean businessRegist(Business busienss) {
        SqlSession session = dbUtil.getSqlSession();
        BusinessRepository businessRepository = session.getMapper(BusinessRepository.class);
        int status = 0;
        try{
            status = businessRepository.addBusiness(busienss);
            session.commit();
        }catch (Exception e) {
            e.printStackTrace();
        }

        if(status != -1) {
            return true;
        }

        return false;
    }

    @Override
    public Business getBusinessByPhoneNumber(String phone_number) {
        SqlSession session = dbUtil.getSqlSession();
        BusinessRepository businessRepository = session.getMapper(BusinessRepository.class);

        try {
            business = businessRepository.findBusinessByPhoneNumber(phone_number);
            session.commit();
        }catch (Exception e) {
            e.printStackTrace();
        }

        return business;
    }

    /**
     * 商家登录
     * @param business
     * @return
     */
    @Override
    public boolean businessLogin(Business business) {
        HashOperations hashOperations = redisTemplate.opsForHash();

        Map map = hashOperations.entries("t_business");

        for(Object obj : map.values()) {
            Business bus = (Business) obj;
            if(bus.getPhone_number().equals(business.getPhone_number())) {
                if(bus.getBusiness_passwd().equals(business.getBusiness_passwd())){
                    //登录成功
                    Cookie cookie = new Cookie("EPTCODE", DesUtil.encrypt(String.valueOf(bus.getBusiness_id())));
                    cookie.setPath("/");
                    cookie.setMaxAge(60 * 60 * 24 * 7); //设置7天过期
                    httpServletResponse.addCookie(cookie);
                    log.info("[登录成功] phone_number = {}", business.getPhone_number());
                    return true;
                }else {
                    log.error("[密码错误] phone_number = {}", business.getPhone_number());
                    return false;
                }
            }
        }

        log.error("[用户不存在] phone_number = {}", business.getPhone_number());
        return false;

    }

    /**
     * 商家账号注销
     * @param business_id
     * @return
     */
    @Override
    public boolean businessLogout(String business_id) {

        Cookie[] cookies = httpServletRequest.getCookies();
        if(cookies != null) {
            for(Cookie cookie : cookies) {
                if(DesUtil.decrypt(cookie.getValue()).equals(business_id)) {
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    httpServletResponse.addCookie(cookie);
                    log.info("[注销成功] busiess_id = {}", business_id);
                    return true;
                }
            }
        }

        log.error("[注销失败] business_id = {}", business_id);
        return false;

    }

    /**
     * 通过电话号码拿到ID
     * @param phone_number
     * @return
     */
    @Override
    public String busPn2Id(String phone_number) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        Map map = hashOperations.entries("t_business");

        for(Object obj : map.values()) {
            Business business = (Business) obj;
            if(business.getPhone_number().equals(phone_number)) {
                return String.valueOf(business.getBusiness_id());
            }
        }
        log.error("[账号不存在] phone_number = {}", phone_number);
        return null;
    }

    /**
     * 上传商家logo
     * @param business_logo
     * @return
     */
    @Override
    public String up7x_business_logo(MultipartFile business_logo){
        String business_id = null;
        Cookie[] cookies = httpServletRequest.getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("EPTCODE")) {
                business_id = DesUtil.decrypt(cookie.getValue());
            }
        }

        //上传文件
        qiniuService.upload(business_logo, Constants.BUCKET_NAME, "business_logo/" + business_id);

        log.info("[商家logo上传] business_id = {}", business_id);

        return QiniuURL + "business_logo/" + business_id;
    }

    /**
     * 上传主页背景
     * @param business_backdrop
     * @return
     */
    @Override
    public String up7x_business_backdrop(MultipartFile business_backdrop) {
        String business_id = null;
        Cookie[] cookies = httpServletRequest.getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("EPTCODE")) {
                business_id = DesUtil.decrypt(cookie.getValue());
            }
        }


        //上传文件
        qiniuService.upload(business_backdrop, Constants.BUCKET_NAME, "business_backdrop/" + business_id);

        log.info("[主页背景上传] business_id = {}", business_id);

        return QiniuURL + "business_backdrop/" + business_id;
    }

    /**
     * 更新商家基本信息
     * @param busInitInfo
     * @return
     */
    @Override
    public boolean businessInfoUpdate(BusInitInfo busInitInfo) {
        int status = 0;
        String business_id = null;


        try {
            Cookie[] cookies = httpServletRequest.getCookies();

            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("EPTCODE")) {
                    business_id = DesUtil.decrypt(cookie.getValue());
                }
            }

            HashOperations hashOperations = redisTemplate.opsForHash();
            business = (Business) hashOperations.get("t_business", business_id);

            assert business != null;
            business.setShop_name(busInitInfo.getShop_name());
            business.setScore("5"); //店铺评分默认5分
            business.setStart_delivery_price(BigDecimal.valueOf(Double.parseDouble(busInitInfo.getStart_delivery_price())));
            business.setDelivery_price(BigDecimal.valueOf(Double.parseDouble(busInitInfo.getDelivery_price())));
            business.setBusiness_longitude(BigDecimal.valueOf(Double.parseDouble(busInitInfo.getBusiness_longitude())));
            business.setBusiness_latitude(BigDecimal.valueOf(Double.parseDouble(busInitInfo.getBusiness_latitude())));
            business.setBusiness_logo(busInitInfo.getBusiness_logoUrl());
            business.setDiscount_info(busInitInfo.getDiscount_info());
            business.setBusiness_notice(busInitInfo.getBusiness_notice());
            business.setBusiness_backdrop(busInitInfo.getBusiness_backdropUrl());
            business.setStatus(1);

            //更新缓存
            hashOperations.put("t_business", business_id, business);

            //更新数据库
            Thread thread = new BusInfoUpdateThread(business, business_id);
            thread.start();
            status = 1;
        }catch (Exception e) {
            status = -1;
            e.printStackTrace();
        }

        return status == 1;
    }

    @Override
    public boolean addCommodity(ComInfo comInfo) {

        HashOperations hashOperations = redisTemplate.opsForHash();

        //获取business_id
        String business_id = null;
        Cookie[] cookies = httpServletRequest.getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("EPTCODE")) {
                business_id = DesUtil.decrypt(cookie.getValue());
            }
        }

        //生成unk
        String add_time = new Date().toString();

        commodity.setBusiness_id(Integer.valueOf(business_id));
        commodity.setCom_des(comInfo.getCom_des());
        commodity.setCom_img(comInfo.getCom_imgUrl());
        commodity.setCom_name(comInfo.getCom_name());
        commodity.setCom_monthly_sales(0);
        commodity.setCom_price(comInfo.getCom_price());
        commodity.setCom_type(comInfo.getCom_type());
        commodity.setComments_pct("100%");
        commodity.setDiscount_price(comInfo.getDiscount_price());
        commodity.setGrp_pchs_num(Integer.valueOf(comInfo.getGrp_pchs_num()));
        commodity.setAdd_time(add_time);

        List imgList = new ArrayList();
        if(comInfo.getDtl_img1Url() != null) {
            imgList.add(comInfo.getDtl_img1Url());
        }
        if(comInfo.getDtl_img2Url() != null) {
            imgList.add(comInfo.getDtl_img2Url());
        }
        if(comInfo.getDtl_img3Url() != null) {
            imgList.add(comInfo.getDtl_img3Url());
        }
        if(comInfo.getDtl_img4Url() != null) {
            imgList.add(comInfo.getDtl_img4Url());
        }
        if(comInfo.getDtl_img5Url() != null) {
            imgList.add(comInfo.getDtl_img5Url());
        }

        //更新数据库
        SqlSession session = dbUtil.getSqlSession();
        BusinessRepository businessRepository = session.getMapper(BusinessRepository.class);

        try {
            businessRepository.addCommodity(commodity);
            commodity = businessRepository.findNewAddedCommodity(business_id, add_time);

            System.out.println(commodity);
            comImg.setCom_id(commodity.getCom_id());
            comImg.setAdd_time(add_time);
            for(int i=0; i<imgList.size(); ++i) {
                comImg.setImg_url(imgList.get(i).toString());
                businessRepository.addComImgs(comImg);
            }

            imgList.clear();

            imgList = businessRepository.findNewAddedImgList(String.valueOf(commodity.getCom_id()), add_time);

            session.commit();
        }catch (Exception e) {
            log.error("[商品添加失败] business_id = {}", business_id);
            e.printStackTrace();
            return false;
        }

        //更新缓存
        hashOperations.put("t_commodity", String.valueOf(commodity.getCom_id()), commodity);

        System.out.println(imgList);

        for(int i=0; i<imgList.size(); ++i) {
            hashOperations.put("t_com_img", String.valueOf(((ComImg)imgList.get(i)).getImg_id()), imgList.get(i));
        }

        return true;
    }

    @Override
    public String up7x_com_img(MultipartFile com_img) {

        String business_id = null;
        Long currentTimeMillis = System.currentTimeMillis();
        Cookie[] cookies = httpServletRequest.getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("EPTCODE")) {
                business_id = DesUtil.decrypt(cookie.getValue());
            }
        }


        //上传文件
        qiniuService.upload(com_img, Constants.BUCKET_NAME, "com_img/" + currentTimeMillis);

        log.info("[商品图片上传] business_id = {}", business_id);

        return QiniuURL + "com_img/" + currentTimeMillis;

    }

    @Override
    public String up7x_dtl_img1(MultipartFile dtl_img1) {
        String business_id = null;
        Long currentTimeMillis = System.currentTimeMillis();
        Cookie[] cookies = httpServletRequest.getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("EPTCODE")) {
                business_id = DesUtil.decrypt(cookie.getValue());
            }
        }


        //上传文件
        qiniuService.upload(dtl_img1, Constants.BUCKET_NAME, "dtl_img1/" + business_id + currentTimeMillis);

        log.info("[详情图一上传] business_id = {}", business_id);

        return QiniuURL + "dtl_img1/" + business_id + currentTimeMillis;
    }

    @Override
    public String up7x_dtl_img2(MultipartFile dtl_img2) {
        String business_id = null;
        Long currentTimeMillis = System.currentTimeMillis();
        Cookie[] cookies = httpServletRequest.getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("EPTCODE")) {
                business_id = DesUtil.decrypt(cookie.getValue());
            }
        }


        //上传文件
        qiniuService.upload(dtl_img2, Constants.BUCKET_NAME, "dtl_img2/" + business_id + currentTimeMillis);

        log.info("[详情图二上传] business_id = {}", business_id);

        return QiniuURL + "dtl_img2/" + business_id + currentTimeMillis;
    }

    @Override
    public String up7x_dtl_img3(MultipartFile dtl_img3) {
        String business_id = null;
        Long currentTimeMillis = System.currentTimeMillis();
        Cookie[] cookies = httpServletRequest.getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("EPTCODE")) {
                business_id = DesUtil.decrypt(cookie.getValue());
            }
        }


        //上传文件
        qiniuService.upload(dtl_img3, Constants.BUCKET_NAME, "dtl_img3/" + business_id + currentTimeMillis);

        log.info("[详情图三上传] business_id = {}", business_id);

        return QiniuURL + "dtl_img3/" + business_id + currentTimeMillis;
    }

    @Override
    public String up7x_dtl_img4(MultipartFile dtl_img4) {
        String business_id = null;
        Long currentTimeMillis = System.currentTimeMillis();
        Cookie[] cookies = httpServletRequest.getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("EPTCODE")) {
                business_id = DesUtil.decrypt(cookie.getValue());
            }
        }

        //上传文件
        qiniuService.upload(dtl_img4, Constants.BUCKET_NAME, "dtl_img4/" + business_id + currentTimeMillis);

        log.info("[详情图四上传] business_id = {}", business_id);

        return QiniuURL + "dtl_img4/" + business_id + currentTimeMillis;
    }

    @Override
    public String up7x_dtl_img5(MultipartFile dtl_img5) {
        String business_id = null;
        Long currentTimeMillis = System.currentTimeMillis();
        Cookie[] cookies = httpServletRequest.getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("EPTCODE")) {
                business_id = DesUtil.decrypt(cookie.getValue());
            }
        }


        //上传文件
        qiniuService.upload(dtl_img5, Constants.BUCKET_NAME, "dtl_img5/" + business_id + currentTimeMillis);

        log.info("[详情图五上传] business_id = {}", business_id);

        return QiniuURL + "dtl_img5/" + business_id + currentTimeMillis;
    }

    /**
     * 商家端查看商品信息
     * @return
     */
    @Override
    public List comLookup() {
        List list = new ArrayList();

        String business_id = null;
        Cookie[] cookies = httpServletRequest.getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("EPTCODE")) {
                business_id = DesUtil.decrypt(cookie.getValue());
            }
        }

        HashOperations hashOperations = redisTemplate.opsForHash();
        Map map = hashOperations.entries("t_commodity");
        for(Object obj : map.values()) {
            Commodity commodity = (Commodity) obj;
            if(String.valueOf(((Commodity) obj).getBusiness_id()).equals(business_id)) {
                ResBusCommodity resBusCommodity = new ResBusCommodity();
                resBusCommodity.setCom_id(String.valueOf(commodity.getCom_id()));
                resBusCommodity.setCom_name(commodity.getCom_name());
                resBusCommodity.setCom_price(commodity.getCom_price());
                resBusCommodity.setDiscount_price(commodity.getDiscount_price());
                resBusCommodity.setCom_des(commodity.getCom_des());
                resBusCommodity.setCom_monthly_sales(String.valueOf(commodity.getCom_monthly_sales()));
                resBusCommodity.setComment_pct(commodity.getComments_pct());
                resBusCommodity.setGrp_pchs_num(String.valueOf(commodity.getGrp_pchs_num()));
                list.add(resBusCommodity);
            }
        }

        if(list.isEmpty()) {
            list.add("null");
        }

        log.info("[商家端::商品查询 business_id = {}]", business_id);

        return list;
    }

    /**
     * 商家端设置拼团信息
     * @param com_id
     * @param start_time
     * @param end_time
     * @return
     */
    @Override
    public boolean setGroup(String com_id, String grpNum, String start_time, String end_time, String freeNum) {
        //获取商家编号
        String business_id = null;
        Cookie[] cookies = httpServletRequest.getCookies();

        List grpList = new ArrayList();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("EPTCODE")) {
                business_id = DesUtil.decrypt(cookie.getValue());
            }
        }

        //按商家编号和商品编号查询该商品，失败返回false
        HashOperations hashOperations = redisTemplate.opsForHash();
        Commodity commodity = (Commodity) hashOperations.get("t_commodity", com_id);
        if(String.valueOf(commodity.getBusiness_id()).equals(business_id)) {

            //生成对应个数空团
            for (int i = 0; i < Integer.valueOf(grpNum); ++i) {
                Group group = new Group();
                group.setGroup_id(business_id + System.currentTimeMillis() + i);
                group.setCom_id(Integer.valueOf(com_id));
                group.setStart_time(TimeUtil.datetimeStrToTimestamp(start_time));
                group.setEnd_time(TimeUtil.datetimeStrToTimestamp(end_time));
                group.setStatus(0);
                group.setFree_num(freeNum);
                grpList.add(group);
            }

            try {
                for (Object obj : grpList) {
                    //将生成的团写入redis
                    hashOperations.put("t_group_list", ((Group) obj).getGroup_id(), obj);
                    //创建新线程，将生成的团写入数据库
                    Thread thread = new GrpInfoUpdateThread((Group) obj);
                    thread.start();
                }

            } catch (Exception e) {
                log.error("[设置拼团失败] business_id = {}", business_id);
                return false;
            }

            log.info("[设置拼团成功] business_id = {}", business_id);
            return true;
        }

        log.error("[设置拼团失败] business_id = {}", business_id);
        return false;
    }

    @Override
    public boolean commodityDel(String com_id) {
        //获取商家编号
        String business_id = null;
        Cookie[] cookies = httpServletRequest.getCookies();
        List del_GrpList = new ArrayList();

        //待删除的join_id
        List sqlMsg_GrpDtl = new ArrayList();

        //待删除的group_id
        List sqlMsg_Grp = new ArrayList();

        //待删除的img_id
        List sqlMsg_Img = new ArrayList();


        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("EPTCODE")) {
                business_id = DesUtil.decrypt(cookie.getValue());
            }
        }

        //获取缓存中商品
        HashOperations hashOperations = redisTemplate.opsForHash();
        commodity = (Commodity) hashOperations.get("t_commodity", com_id);
        Map imgMap = hashOperations.entries("t_com_img");
        //安全检查
        if(!String.valueOf(commodity.getBusiness_id()).equals(business_id)) {
            log.error("[商品下架失败] com_id = {}", com_id);
            return false;
        }

        //检查是否可删除
        Map GrpMap = hashOperations.entries("t_group_list");

        for(Object obj : GrpMap.values()) {
            Group group = (Group) obj;
            //如果有未完成拼团，商品无法下架
            if(String.valueOf(group.getCom_id()).equals(com_id) && group.getStatus() != 3) {
                log.error("[商品下架失败] com_id = {}", com_id);
                return false;
            }
        }

        //删除操作

        //缓存部分

        //预留    --  删除用户收藏
        //预留    --  删除评论信息
        //删除拼团表信息
        for(Object obj : GrpMap.values()) {
            Group group = (Group) obj;
            if(String.valueOf(group.getCom_id()).equals(com_id)) {
                del_GrpList.add(((Group) obj).getGroup_id());
            }
        }
        for(int i=0; i<del_GrpList.size(); ++i) {
            sqlMsg_Grp.add(del_GrpList.get(i));
            if(hashOperations.delete("t_group_list", del_GrpList.get(i)) != 1) {
                log.error("[商品下架失败] com_id = {}", com_id);
                return false;
            }
        }

        //删除拼团详情表信息
        Map GrpDtlMap = hashOperations.entries("t_group_dtl");
        for(Object obj : GrpDtlMap.values()) {
            GroupDtl groupDtlMap = (GroupDtl) obj;
            if(del_GrpList.contains(groupDtlMap.getGroup_id())) {
                sqlMsg_GrpDtl.add(groupDtlMap.getJoin_id());
                if(hashOperations.delete("t_group_dtl", groupDtlMap.getJoin_id()) != 1){
                    log.error("[商品下架失败] com_id = {}", com_id);
                    return false;
                }
            }
        }

        //删除商品图片

        for(Object obj : imgMap.values()) {
            ComImg comImg = (ComImg) obj;
            if(String.valueOf(comImg.getCom_id()).equals(com_id)) {
                sqlMsg_Img.add(comImg.getImg_id());
                if(hashOperations.delete("t_com_img", String.valueOf(comImg.getImg_id())) != 1) {
                    log.error("[商品下架失败] com_id = {}", com_id);
                    return false;
                }
            }
        }

        //删除商品
        Long status = hashOperations.delete("t_commodity", com_id);

        if(status == 1) {

            //数据库部分 新线程
            Thread thread = new ComDelThread(sqlMsg_GrpDtl, sqlMsg_Grp, sqlMsg_Img, com_id);
            thread.start();

            //七牛云   --  删除商品图片
            String com_img = commodity.getCom_img();
            List imgList = new ArrayList();


            //筛选待删除商品图片
            for(Object obj : imgMap.values()) {
                comImg = (ComImg) obj;
                if(String.valueOf(comImg.getCom_id()).equals(com_id)) {
                    imgList.add(obj);
                }
            }

            qiniuService.deleteFile(com_img.substring(22));

            //获取待删除图片url
            if(!imgList.isEmpty()) {
                for(int i=0; i < imgList.size(); ++i) {
                    qiniuService.deleteFile(((ComImg)imgList.get(i)).getImg_url().substring(22));
                }
            }

            log.info("[商品下架成功] com_id = {}", com_id);
            return true;
        }

        log.error("[商品下架失败] com_id = {}", com_id);
        return false;
    }

    @Override
    public Object grpInfoLookup() {

        String business_id = null;
        Cookie[] cookies = httpServletRequest.getCookies();
        List result = new ArrayList();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("EPTCODE")) {
                business_id = DesUtil.decrypt(cookie.getValue());
            }
        }

        HashOperations hashOperations = redisTemplate.opsForHash();
        Map allGrp = hashOperations.entries("t_group_list");
        Map allCommodity = hashOperations.entries("t_commodity");
        Map allGrpDtl = hashOperations.entries("t_group_dtl");
        List selectedComId = new ArrayList();
        List selectedCommodity = new ArrayList();
        List selectedGroup = new ArrayList();

        for(Object obj : allCommodity.values()) {
            Commodity commodity = (Commodity) obj;
            if(String.valueOf(commodity.getBusiness_id()).equals(business_id)) {
                selectedComId.add(((Commodity) obj).getCom_id());
                selectedCommodity.add(obj);
            }
        }

        for(Object obj : allGrp.values()) {
            Group group = (Group) obj;
            if(selectedComId.contains(group.getCom_id())) {
                selectedGroup.add(obj);
            }
        }

        for(int i=0; i<selectedGroup.size(); ++i) {
            ResBusGrpInfo resBusGrpInfo = new ResBusGrpInfo();
            int presentNum = 0;
            resBusGrpInfo.setGroup_id(((Group) selectedGroup.get(i)).getGroup_id());
            resBusGrpInfo.setCom_id(String.valueOf(((Group) selectedGroup.get(i)).getCom_id()));
            resBusGrpInfo.setStart_time(String.valueOf(((Group) selectedGroup.get(i)).getStart_time()).substring(0, 19));
            resBusGrpInfo.setEnd_time(String.valueOf(((Group) selectedGroup.get(i)).getEnd_time()).substring(0, 19));
            resBusGrpInfo.setStatus(String.valueOf(((Group) selectedGroup.get(i)).getStatus()));
            for(int j=0; j<selectedCommodity.size(); ++j) {
                if(((Group) selectedGroup.get(i)).getCom_id()
                        == ((Commodity) selectedCommodity.get(j)).getCom_id()) {
                    resBusGrpInfo.setCom_name(((Commodity) selectedCommodity.get(j)).getCom_name());
                    resBusGrpInfo.setGrp_pchs_num(String.valueOf(((Commodity) selectedCommodity.get(j)).getGrp_pchs_num()));
                    for(Object obj:allGrpDtl.values()) {
                        if(((GroupDtl) obj).getGroup_id().equals(resBusGrpInfo.getGroup_id())) {
                            presentNum ++;
                        }
                    }
                    break;
                }
            }
            resBusGrpInfo.setPresentNum(String.valueOf(presentNum));
            result.add(resBusGrpInfo);
        }

        log.info("[商家端::拼团信息已返回] business_id = {}", business_id);
        return result;
    }

    /**
     * 拼团删除
     * @param group_id
     * @return
     */
    @Override
    public boolean grpDelete(String group_id) {

        int status = 0;
        Map allGrpDtl;
        List selectedDrpDtl = new ArrayList();

        //检查拼团是否存在且status=0
        HashOperations hashOperations = redisTemplate.opsForHash();
        Group group = (Group) hashOperations.get("t_group_list", group_id);

        if(group.getStatus() == 2 || group.getStatus() == 3) {
            allGrpDtl = hashOperations.entries("t_group_dtl");
            for(Object object : allGrpDtl.values()) {
                if(((GroupDtl) object).getGroup_id().equals(group_id)) {
                    selectedDrpDtl.add(object);
                    hashOperations.delete("t_group_dtl", ((GroupDtl) object).getGroup_id());
                }
            }

        } else {
            if(group == null || group.getStatus() != 0) {
                return false;
            }

            //检查该拼团对应的拼团详情表是否为空
            Map allGrpdtl = hashOperations.entries("t_group_dtl");
            for(Object obj : allGrpdtl.values()) {
                GroupDtl groupDtl = (GroupDtl) obj;
                if (groupDtl.getGroup_id().equals(group_id))
                    return false;
            }
        }

        //删除redis数据
        hashOperations.delete("t_group_list", group_id);
        //删除数据库数据
        SqlSession session = dbUtil.getSqlSession();
        BusinessRepository businessRepository = session.getMapper(BusinessRepository.class);

        try {
            for(int i=0; i<selectedDrpDtl.size(); ++i) {
                if(status != -1) {
                    status = businessRepository.delFromGrpDtl(((GroupDtl) selectedDrpDtl.get(i)).getJoin_id());
                }
            }
            status = businessRepository.grpDelete(group_id);
        }catch (Exception e) {
            e.printStackTrace();
            log.error("[删除拼团失败] group_id = {}", group_id);
        }

        if(status != -1) {
            session.commit();
            log.info("[删除拼团成功] group_id = {}", group_id);
            return true;
        }

        return false;
    }
}
