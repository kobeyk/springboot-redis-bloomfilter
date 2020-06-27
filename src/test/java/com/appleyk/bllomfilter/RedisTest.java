package com.appleyk.bllomfilter;

import com.appleyk.App;
import com.appleyk.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.TimeUnit;

/**
 * <p>Redis使用测试</p>
 *
 * @author appleyk
 * @version V.0.1.1
 * @blob https://blog.csdn.net/appleyk
 * @date created on 2020/5/19 10:46 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = App.class)
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void redisTest() throws Exception {
        Object value = redisTemplate.opsForValue().get("name");
        checkValue(value);
        int seconds = 3;
        redisTemplate.opsForValue().set("name", "appleyk", seconds, TimeUnit.SECONDS);
        value = redisTemplate.opsForValue().get("name");
        checkValue(value);
        TimeUnit.SECONDS.sleep(seconds);
        value = redisTemplate.opsForValue().get("name");
        checkValue(value);
    }

    public void checkValue(Object value) {
        if (value == null) {
            System.out.println("key(name)找不到对应的value。");
        } else {
            System.out.println("key(name) = " + value.toString());
        }
    }

}
