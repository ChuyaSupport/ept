package com.ept.powersupport.resObj;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class ResComment {

    //用户头像
    private String user_profile;

    //用户昵称
    private String user_nickname;

    //评论时间
    private String comment_time;

    //评论内容
    private String content;

}
