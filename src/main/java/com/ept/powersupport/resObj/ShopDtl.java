package com.ept.powersupport.resObj;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class ShopDtl {
    //店铺名称
    private String shop_name;

    //配送时间
    private String delivery_time;

    //月销售量
    private String monthly_sales;

    //优惠信息
    private String discount_info;

    //商家logo
    private String business_logo;

    //商家公告
    private String business_notice;

    //详情页背景
    private String business_backdrop;

    //在拼人数
    private String grouping_now;

    //商品列表
    private List<ResCommodity> commodityList;

    //关注信息
    private String follow;
}
