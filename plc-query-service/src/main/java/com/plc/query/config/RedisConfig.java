package com.plc.query.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.example.industrial.contracts.event.PlcReadingEvent;
import com.plc.query.domain.PlcReading;

@Configuration
public class RedisConfig {

    @Bean
    RedisTemplate<String, PlcReadingEvent> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, PlcReadingEvent> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        // KEY als String
        template.setKeySerializer(new StringRedisSerializer());

        template.setValueSerializer(new JacksonJsonRedisSerializer<>(PlcReading.class));

        template.afterPropertiesSet();
        return template;
    }
}