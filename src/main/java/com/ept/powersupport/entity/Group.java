package com.ept.powersupport.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Data
@Component
public class Group {

    private String group_id;
    private int com_id;
    private Timestamp start_time;
    private Timestamp end_time;
    private int status;
    private String free_num;

    /**
     * status说明
     * 未开始  -1
     * 正在拼团 0
     * 拼团结束 1
     */

}
