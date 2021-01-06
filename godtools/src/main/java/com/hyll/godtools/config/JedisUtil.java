package com.hyll.godtools.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

@Component
public class JedisUtil {
    private Jedis jedis;
    @Value(value = "${spring.redis.host}")
    private String host;
    @Value(value = "${spring.redis.port}")
    private Integer port;

    public  synchronized Jedis getJedis(){
        if(jedis==null){
            jedis = new Jedis(host,port);
        }
        return jedis;
    }

}
