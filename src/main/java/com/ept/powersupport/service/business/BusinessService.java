package com.ept.powersupport.service.business;

import com.ept.powersupport.entity.Business;
import com.ept.powersupport.reqObj.BusInitInfo;
import com.ept.powersupport.reqObj.ComInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BusinessService {
    boolean checkIsExisted(String phone_number);
    boolean businessRegist(Business busienss);
    Business getBusinessByPhoneNumber(String phone_number);
    boolean businessLogin(Business business);
    boolean businessLogout(String business_id);
    String busPn2Id(String phone_number);
    String up7x_business_logo(MultipartFile business_logo);
    String up7x_business_backdrop(MultipartFile business_logo);
    boolean businessInfoUpdate(BusInitInfo busInitInfo);
    boolean addCommodity(ComInfo comInfo);
    String up7x_com_img(MultipartFile com_img);
    String up7x_dtl_img1(MultipartFile dtl_img1);
    String up7x_dtl_img2(MultipartFile dtl_img2);
    String up7x_dtl_img3(MultipartFile dtl_img3);
    String up7x_dtl_img4(MultipartFile dtl_img4);
    String up7x_dtl_img5(MultipartFile dtl_img5);
    List comLookup();
    boolean setGroup(String com_id, String grpNum, String start_time, String end_time, String freeNum);
    boolean commodityDel(String com_id);
    Object grpInfoLookup();
    boolean grpDelete(String group_id);
}
