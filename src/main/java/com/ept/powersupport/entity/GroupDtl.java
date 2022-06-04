package com.ept.powersupport.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class GroupDtl {
    private String join_id;
    private String group_id;
    private String openid;
    private int status;
    private int is_free;
}
