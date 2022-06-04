package com.ept.powersupport.resObj;

import com.ept.powersupport.entity.Coupon;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class ResMyOrder {
    private String business_logo;
    private String shop_name;
    private String status;    //0等待拼团完成  1拼团失败  2拼团成功等待发货  3已签收
    private String com_img;
    private String com_name;
    private String com_price;
    private Coupon coupon;
    private String realCost;
    private String com_id;
}
