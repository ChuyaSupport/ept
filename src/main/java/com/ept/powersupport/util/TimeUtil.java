package com.ept.powersupport.util;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class TimeUtil {

    public static Timestamp datetimeStrToTimestamp(String datetimeStr) {

        Date date = new Date();
        String str= datetimeStr.replace("T", " ");

        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            date = sdf.parse(str);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return new Timestamp(date.getTime());
    }
}
