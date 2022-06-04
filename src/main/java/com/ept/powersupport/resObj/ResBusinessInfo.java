package com.ept.powersupport.resObj;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ResBusinessInfo {
    private String business_id;
    private String shop_name;
    private String score;
    private String monthly_sales;
    private String delivery_time;
    private String start_delivery_price;
    private String delivery_price;
    private String delivery_distance;
    private String discount_info;
    private String business_logo;
}
