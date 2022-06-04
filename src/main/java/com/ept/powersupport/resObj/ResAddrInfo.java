package com.ept.powersupport.resObj;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class ResAddrInfo {

    private String addr_id;
    private String name;
    private String gender;
    private String phone;
    private String addr;
    private String validPresent;

}
