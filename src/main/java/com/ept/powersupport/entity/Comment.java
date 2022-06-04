package com.ept.powersupport.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Data
@Component
public class Comment {

    private int comment_id;
    private int com_id;
    private String openid;
    private Timestamp comment_time;
    private String content;

}
