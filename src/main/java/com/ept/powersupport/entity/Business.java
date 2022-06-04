package com.ept.powersupport.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Data
@Component
public class Business {

    //商家编号
    private int business_id;

    //商家账号(手机号)
    private String phone_number;

    //商家密码
    private String business_passwd;

    //店名
    private String shop_name;

    //店铺评分
    private String score;

    //月销售量
    private int monthly_sales;

    //起送价格
    private BigDecimal start_delivery_price;

    //配送价格
    private BigDecimal delivery_price;

    //经度
    private BigDecimal business_longitude;

    //纬度
    private BigDecimal business_latitude;

    //商家logo
    private String business_logo;

    //优惠信息
    private String discount_info;

    //商家公告
    private String business_notice;

    //详情页背景
    private String business_backdrop;

    //店铺状态  0关闭     1营业
    private int status;
}
