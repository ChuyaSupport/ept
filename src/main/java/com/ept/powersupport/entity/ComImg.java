package com.ept.powersupport.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ComImg {
    //图片编号
    int img_id;

    //商品编号
    int com_id;

    //图片url
    private String img_url;

    //添加时间
    private String add_time;
}
