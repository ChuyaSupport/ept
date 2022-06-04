package com.ept.powersupport.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class Follow {
    private String follow_id;
    private String openid;
    private String business_id;
}
