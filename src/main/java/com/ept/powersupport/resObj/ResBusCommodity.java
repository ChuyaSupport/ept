package com.ept.powersupport.resObj;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 已上架商品返回体
 */
@Data
@Component
public class ResBusCommodity {

    //商品编号
    private String com_id;
    //商品名称
    private String com_name;
    //商品价格
    private String com_price;
    //优惠券
    private String discount_price;
    //商品描述
    private String com_des;
    //月销售量
    private String com_monthly_sales;
    //好评率
    private String comment_pct;
    //团购人数
    private String grp_pchs_num;
}
