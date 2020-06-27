package com.appleyk.thread;

import com.appleyk.dao.entity.UserEntity;
import com.appleyk.service.UserService;
import com.google.common.hash.BloomFilter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 * <p>自定义线程，模拟基于订单号查询</p>
 *
 * @author appleyk
 * @version V.0.1.1
 * @blob https://blog.csdn.net/appleyk
 * @date created on 2020/5/19 10:46 PM
 */
public class MyThread implements Runnable {

    public StringRedisTemplate redisTemplate;
    public CyclicBarrier barrier;
    private BloomFilter<String> bf;
    private UserService userService;

    public MyThread(StringRedisTemplate redisTemplate, CyclicBarrier barrier,
                    BloomFilter<String> bf, UserService userService) {
        this.redisTemplate = redisTemplate;
        this.barrier = barrier;
        this.bf = bf;
        this.userService = userService;
    }

    @Override
    public void run() {
        try {
            // 等所有线程准备就绪后，一起执行
            barrier.await();
        } catch (Exception e) {
            System.out.println(e);
        }
        String threadName = Thread.currentThread().getName();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String uuid = UUID.randomUUID().toString();
        String key = "key:" + uuid;
        // 1、先布隆过滤器过滤一把
        if (!bf.mightContain(uuid)) {
            System.out.println(simpleDateFormat.format(new Date())+" -- 布隆过滤器中不存在，疑似非法请求！");
            return;
        }
        // 2、如果布隆过滤器没有挡住（有可能存在误判），则拿着key去查redis
        Object value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            System.out.println(threadName + "," + simpleDateFormat.format(new Date()) + "-- 缓存命中，key = " + key);
            return;
        }
        // 3、如果redis中没有，则去数据库查（这个时候，高并发下最容易发生数据库连接不够用的情况）
        UserEntity user = null;
        try {
            user = userService.query(uuid);
        } catch (Exception e) {
            System.out.println(e);
        }
        if (user != null) {
            System.out.println(threadName + "," + "uuid = " + uuid + "," + simpleDateFormat.format(new Date()) + " -- 在数据库中查到，准备写缓存...");
            // 3.1 如果查到的话，往redis中写
            redisTemplate.opsForValue().set(key, uuid, 60, TimeUnit.SECONDS);
            System.out.println(threadName + "," + "缓存写入成功！");
        } else {
            // 3.2 如果数据库中也没有的话，往redis中写个空串
            System.out.println(threadName + "," + "uuid = " + uuid + " -- 在redis未找到,在数据库中也未查到，发生缓存穿透！" + simpleDateFormat.format(new Date()));
            // 3.3 如果没有布隆过滤器在redis前面过滤一把，这个地方很有可能被恶意的请求击穿redis
            redisTemplate.opsForValue().set(key, "empty", 60, TimeUnit.SECONDS);
        }

    }
}
