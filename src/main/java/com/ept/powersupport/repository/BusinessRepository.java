package com.ept.powersupport.repository;

import com.ept.powersupport.entity.Business;
import com.ept.powersupport.entity.ComImg;
import com.ept.powersupport.entity.Commodity;
import com.ept.powersupport.entity.Group;
import com.ept.powersupport.reqObj.BusInitInfo;
import com.ept.powersupport.reqObj.ComInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BusinessRepository {
    String checkIsPhoneNumberExisted(@Param("phone_number") String phone_number);
    int addBusiness(Business business);
    Business findBusinessByPhoneNumber(@Param("phone_number") String phone_number);
    int update(Business business);
    int addCommodity(Commodity commodity);
    int addComImgs(ComImg comImg);
    Commodity findNewAddedCommodity(@Param("business_id") String business_id, @Param("add_time") String add_time);
    List findNewAddedImgList(@Param("com_id") String com_id, @Param("add_time") String add_time);
    int setGrp(Group group);
    int delFromGrpDtl(String join_id);
    int delFromGrpList(String group_id);
    int delFromComImg(String img_id);
    int delFromCommodity(String com_id);
    int grpDelete(String group_id);
}
