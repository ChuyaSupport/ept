package com.ept.powersupport.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Data
@Component
public class Coupon {

    private String coupon_id;   //券编号
    private String openid;
    private String business_id;
    private BigDecimal value;       //券面值
    private String status;

}
