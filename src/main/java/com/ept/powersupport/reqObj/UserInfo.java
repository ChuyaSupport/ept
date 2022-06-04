package com.ept.powersupport.reqObj;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class UserInfo {

    //用户昵称
    private String nickName;

    //用户头像
    private String avatarUrl;

    /**
     * 用户性别
     * 0    未知
     * 1    男性
     *2    女性
     */
    private int gender;

    //用户所在国家
    private String country;

    //用户所在省份
    private String province;

    //用户所在城市
    private String city;

    /**
     * 显示country，province, city所用的语言
     * language的合法值
     * en	    英文
     * zh_CN	简体中文
     * zh_TW	繁体中文
     */
    private String language;

}
