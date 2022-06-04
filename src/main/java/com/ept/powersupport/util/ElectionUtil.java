package com.ept.powersupport.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
@Slf4j
public class ElectionUtil {

    public Map elect(int num, int eleNum) {

        Random random = new Random();

        Map map = new HashMap();

        for(int i=0; i<num; ++i) {
            map.put(i, 0);
        }

        for(int i=0; i<eleNum; ++i) {
            int e = random.nextInt(num);

            if ((int)map.get(e) == 1) {
                i--;
                continue;
            }

            map.put(e, 1);
        }

        return map;
    }
}
