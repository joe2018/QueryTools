package com.hyll.godtools.Timer;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CreateCache;
import com.hyll.godtools.pojo.chche.CacheSequenceId;
import com.hyll.godtools.util.JedisUtil;
import com.hyll.godtools.pojo.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.Date;
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
     * 每隔一个小时清除一次数据
     */
    @Async
//    @Scheduled(cron = "0 0 0 1/1 * ?")
//    @Scheduled(cron = "0 50 10 1/1 * ?")
    @Scheduled(cron = "0 0 1/1 * * ?")
    public void clearCompareDate(){
        try {
            Jedis jedis = jedisUtil.getJedis();
            Set<String> keys = jedis.keys(Constant.SEQUENCE_ID + "*");
            Set<String> strSet = new HashSet<>();
            keys.stream().forEach(f->{
                strSet.add(f.substring(Constant.SEQUENCE_ID.length()));
            });
            Map<String, String> all = cacheSequenceId.getAll(strSet);
            Date date = DateUtil.date();
            Set<String> delKey = new HashSet<>();
            //如果创建时间超过一个小时，则清除掉
            all.keySet().stream().forEach(f->{
                CacheSequenceId cacheSequenceId = JSONUtil.toBean(all.get(f), CacheSequenceId.class);
                if(DateUtil.between(DateUtil.parse(cacheSequenceId.getCreateTime()),date, DateUnit.HOUR)>1){
                    delKey.add(f);
                }
            });
            if(delKey.size()>0){
                cacheSequenceId.removeAll(delKey);
            }
            log.info("清除缓存数据成功");
        }catch (Exception e){
            e.printStackTrace();
            log.error("清除缓存数据失败");
        }


    }
}
