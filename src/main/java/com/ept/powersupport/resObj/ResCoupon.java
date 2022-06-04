package com.ept.powersupport.resObj;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class ResCoupon {

    private String coupon_id;
    private String value;
}
