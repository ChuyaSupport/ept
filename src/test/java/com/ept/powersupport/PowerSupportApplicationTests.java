package com.ept.powersupport;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@SpringBootTest
public class PowerSupportApplicationTests {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

//    @Test
//    void contextLoads() {
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        valueOperations.set("name", "zhangsan");
//        Object name = valueOperations.get("name");
//
//        System.out.println(name);
//    }

}
