package com.ept.powersupport.reqObj;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ComInfo {

    //商品名称
    private String com_name;

    //商品描述
    private String com_des;

    //商品价格
    private String com_price;

    //折扣价格
    private String discount_price;

    //商品图片，主图
    private String com_imgUrl;

    //商品类别
    private String com_type;

    //成团人数
    private String grp_pchs_num;

    //详情图1
    private String dtl_img1Url;

    //详情图2
    private String dtl_img2Url;

    //详情图3
    private String dtl_img3Url;

    //详情图4
    private String dtl_img4Url;

    //详情图5
    private String dtl_img5Url;

}
