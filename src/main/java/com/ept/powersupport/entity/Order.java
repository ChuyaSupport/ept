package com.ept.powersupport.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Data
@Component
public class Order {

    private String order_id;
    private String join_id;
    private String coupon_id;   //优惠券编号
    private String addr_id;
    private String note;          //备注信息
    private String tableware;   //餐具信息
    private String paid;        //未付款0  已付款1
    private BigDecimal price;       //应支付价格
}
