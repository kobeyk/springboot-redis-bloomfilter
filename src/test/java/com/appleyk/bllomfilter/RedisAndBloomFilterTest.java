package com.appleyk.bllomfilter;

import com.appleyk.App;
import com.appleyk.dao.entity.UserEntity;
import com.appleyk.service.UserService;
import com.appleyk.thread.MyThread;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.*;

/**
 * <p>Redis配合布隆过滤器进行使用</p>
 *
 * @author appleyk
 * @version V.0.1.1
 * @blob https://blog.csdn.net/appleyk
 * @date created on 2020/5/19 10:46 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = App.class)
public class RedisAndBloomFilterTest {

    @Autowired
    private UserService userService;

    //@Autowired
    //private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public static BloomFilter<String> bf;

    @Test
    public void count() throws Exception {
        System.out.println("用户数量：" + userService.count());
    }

    @PostConstruct
    public void init(){
        long start = System.currentTimeMillis();
        List<UserEntity> userEntities = userService.queryAll();
        if(userEntities !=null && userEntities.size()>0){
            bf = BloomFilter.create(Funnels.stringFunnel(Charset.forName("uTF-8")), userEntities.size());
            userEntities.forEach(u->{
                // 将查询（离线的）的所有订单号全部存入布隆过滤器中
                bf.put(u.getUuid());
            });
        }
        long end = System.currentTimeMillis();
        System.out.println("初始化离线订单号耗时："+(end-start)+"ms");
    }

    @Test
    public void queryTest() throws Exception{
        int concurrent = 1000;
        // 利用循环栅栏(CountDownLatch是一次性的)，来实现1000个线程同时并发工作（好比，吃饭，人到齐了才上菜）
        CyclicBarrier barrier = new CyclicBarrier(concurrent);
        // 生成1000个固定的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(concurrent);
        long start = System.currentTimeMillis();
        for (int i = 0; i < concurrent; i++) {
            executorService.execute(new MyThread(redisTemplate,barrier,bf,userService));
        }
        /** 当线程池调用该方法时,线程池的状态则立刻变成SHUTDOWN状态。
         * 此时，则不能再往线程池中添加任何任务，否则将会抛出RejectedExecutionException异常。
         * 但是，此时线程池不会立刻退出，直到添加到线程池中的所有任务都已经处理完成，才会退出。
         */
        executorService.shutdown();

        // 判断线程池中的任务是否全部执行完
        while (!executorService.isTerminated()){

        }
        long end = System.currentTimeMillis();
        System.out.println("1000个线程并发查询数据库，耗时："+(end-start)+"ms");
    }

}
