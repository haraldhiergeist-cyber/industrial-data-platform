package com.plc.query.application.current;

import java.util.List;
import java.util.Optional;

import com.example.industrial.contracts.event.PlcReadingEvent;

public interface CurrentValueRepository {
	Optional<PlcReadingEvent> findByTag(String tag);
    void save(String key,PlcReadingEvent reading);
    List<PlcReadingEvent> findAll();
    void delete(String tag);
    public default String buildRedisKey(String source, String tagName) {
        return "plc:%s:%s".formatted(source, tagName);
    }
}