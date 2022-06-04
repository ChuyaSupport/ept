package com.ept.powersupport.resObj;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class ResOrderContent {

    private String shop_name;
    private String com_name;
    private String com_img;
    private String com_price;
}
