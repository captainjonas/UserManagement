package com.wiredcraft.config;


import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.redisson.config.Config;

/**
 * @author Eric Yao
 * @date 2022-11-06
 */
@Configuration
public class RedissonConfig {

    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private String port;
    @Value("${spring.redis.password}")
    private String password;

    @Bean
    public RedissonClient getRedisSon() {
        Config config = new Config();
        String address = new StringBuilder("redis://").append(host).append(":").append(port).toString();
        config.useSingleServer().setAddress(address);
        if (null != password && !"".equals(password.trim())) {
            config.useSingleServer().setPassword(password);
        }
        return Redisson.create(config);
    }

}
