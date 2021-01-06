package com.hyll.godtools.Timer;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CreateCache;
import com.hyll.godtools.config.JedisUtil;
import com.hyll.godtools.pojo.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 定时器
 */
@Component
@EnableAsync
@Slf4j
public class Timer {

    @Autowired
    private JedisUtil jedisUtil;
    @CreateCache(name = Constant.SEQUENCE_ID)
    private Cache<String,String> cacheSequenceId;
    /**
     * 每天晚上定时清除比对数据
     */
    @Async
//    @Scheduled(cron = "0 0 0 1/1 * ?")
    @Scheduled(cron = "0 14 15 1/1 * ?")
    public void clearCompareDate(){
        try {
            Jedis jedis = jedisUtil.getJedis();
            Set<String> keys = jedis.keys(Constant.SEQUENCE_ID + "*");
            Set<String> strSet = new HashSet<>();
            keys.stream().forEach(f->{
                strSet.add(f.substring(Constant.SEQUENCE_ID.length()));
            });
            Map<String, String> all = cacheSequenceId.getAll(strSet);
            Set<String> stringSet = all.keySet();
            stringSet.stream().forEach(f->{
                jedis.del(f);
            });
            cacheSequenceId.removeAll(stringSet);
            log.info("清除缓存数据成功");
        }catch (Exception e){
            e.printStackTrace();
            log.error("清除缓存数据失败");
        }


    }
}
