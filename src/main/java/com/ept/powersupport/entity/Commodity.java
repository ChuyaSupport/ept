package com.ept.powersupport.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class Commodity {

    //商品编号
    private int com_id;

    //商家编号
    private int business_id;

    //商品名称
    private String com_name;

    //月销售量
    private int com_monthly_sales;

    //商品描述
    private String com_des;

    //好评率
    private String comments_pct;

    //商品价格
    private String com_price;

    //商品图片

    private String com_img;

    //商品类别
    private String com_type;

    //优惠券
    private String discount_price;

    //拼团购买人数
    private int grp_pchs_num;

    //记录标识，用于添加入库后取出记录
    private String add_time;

}
