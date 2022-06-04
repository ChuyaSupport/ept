package com.ept.powersupport.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class UserAddr {

    //经度
    private String user_longitude;

    //纬度
    private String user_latitude;

    //地址编号
    private String addr_id;

    //openid
    private String openid;

    //收货人姓名
    private String name;

    //收货人性别
    private String gender;

    //收货人电话号码
    private String phone;

    //收货地址
    private String addr;

    //最近使用
    private String recently_used;
}
