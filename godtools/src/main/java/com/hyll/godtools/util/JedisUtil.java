package com.hyll.godtools.util;

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
    @Value(value = "${spring.redis.password}")
    private String password;

    public  synchronized Jedis getJedis(){
        if(jedis==null){
            jedis = new Jedis(host,port);
            jedis.auth(password);
        }
        return jedis;
    }

}
