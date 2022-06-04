package com.ept.powersupport.resObj;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class ResBusGrpInfo {

    private String group_id;
    private String com_id;
    private String com_name;
    private String start_time;
    private String end_time;
    private String presentNum;
    private String grp_pchs_num;
    private String status;
}
