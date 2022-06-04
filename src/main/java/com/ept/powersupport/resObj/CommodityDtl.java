package com.ept.powersupport.resObj;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class CommodityDtl {
    //商品图片列表
    private List comImgList;

    //商品编号
    private String com_id;

    //商品价格
    private String com_price;

    //券后价格
    private String final_price;

    //今日拼单数
    private String daily_group;

    //商品名称
    private String com_name;

    //商品描述
    private String com_des;

    //商家配送·十人成团·两人免单
    private String discount_str;

    //评价列表
    private List<ResComment> comment_list;

    /**
     * 商家信息
     */

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

}
