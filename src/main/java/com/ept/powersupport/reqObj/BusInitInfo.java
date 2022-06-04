package com.ept.powersupport.reqObj;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class BusInitInfo {
    private String business_logoUrl;
    private String business_backdropUrl;
    private String shop_name;
    private String monthly_sales;
    private String start_delivery_price;
    private String delivery_price;
    private String discount_info;
    private String business_notice;
    private String business_longitude;
    private String business_latitude;
}
