package com.ept.powersupport.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Data
@Component
public class User {

    //用户编号
    private int user_id;

    //openid
    private String openid;

    //用户名
    private String user_name;

    //用户头像
    private String user_profile;

    //经度
    private BigDecimal user_longitude;

    //纬度
    private BigDecimal user_latitude;

    //余额
    private BigDecimal balance;
}
