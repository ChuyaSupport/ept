package com.ept.powersupport.resObj;


import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class ResCommodity {

    //商品编号
    private String com_id;

    //商品名称
    private String com_name;

    //月销售量
    private String com_monthly_sales;

    //商品描述
    private String com_des;

    //好评率
    private String comments_pct;

    //商品价格
    private String com_price;

    //商品图片
    private String com_img;

    //商品类别
    private String com_type;

}
