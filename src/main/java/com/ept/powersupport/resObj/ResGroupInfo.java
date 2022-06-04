package com.ept.powersupport.resObj;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class ResGroupInfo {

    //团编号
    private String group_id;

    //开始时间
    private String start_time;

    //结束时间
    private String end_time;

    //拼团开始倒计时
    private String toStartTime;

    //拼团结束倒计时
    private String available_time;


    //可加入数
    private String available_num;

    //团用户列表
    private List<ResGroupUser> user_list;

    //本用户在团状态
    private String isInGrp;

}
