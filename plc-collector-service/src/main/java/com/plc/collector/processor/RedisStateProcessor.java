package com.plc.collector.processor;


import com.example.industrial.contracts.event.PlcReadingEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisStateProcessor implements Processor {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisStateProcessor(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        PlcReadingEvent event = exchange.getMessage().getBody(PlcReadingEvent.class);

        String key = "plc:source:%s:tag:%s".formatted(
                sanitize(event.source()),
                sanitize(event.tagName())
        );

        String json = objectMapper.writeValueAsString(event);

        redisTemplate.opsForValue().set(key, json);

        exchange.getMessage().setHeader("redis.key", key);
    }

    private String sanitize(String input) {
        return input == null ? "unknown" : input.replace(" ", "_");
    }
}