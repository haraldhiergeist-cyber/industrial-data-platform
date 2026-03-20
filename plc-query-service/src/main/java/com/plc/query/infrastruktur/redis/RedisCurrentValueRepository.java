package com.plc.query.infrastruktur.redis;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.example.industrial.contracts.event.PlcReadingEvent;
import com.plc.query.application.current.CurrentValueRepository;

@Repository
public class RedisCurrentValueRepository implements CurrentValueRepository {

    private final RedisTemplate<String, PlcReadingEvent> redisTemplate;

    public RedisCurrentValueRepository(RedisTemplate<String, PlcReadingEvent> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Optional<PlcReadingEvent> findByTag(String tag) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(tag));
    }
    
    @Override
    public void save(String key, PlcReadingEvent reading) {
        redisTemplate.opsForValue().set(key, reading);
    }
    
    @Override
    public List<PlcReadingEvent> findAll() {
        Set<String> keys = redisTemplate.keys("*");

        if (keys == null || keys.isEmpty()) {
            return List.of();
        }

        return redisTemplate.opsForValue().multiGet(keys);
    }
    
    @Override
    public void delete(String tag) {
    	redisTemplate.delete(tag);
    }
}