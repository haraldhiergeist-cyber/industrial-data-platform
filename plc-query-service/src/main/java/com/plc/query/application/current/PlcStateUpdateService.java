package com.plc.query.application.current;


import com.example.industrial.contracts.event.PlcReadingEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class PlcStateUpdateService {

    private final CurrentValueRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    public PlcStateUpdateService(
    		CurrentValueRepository repository,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.repository = repository;
        this.messagingTemplate = messagingTemplate;
    }

    public void handleIncomingReading(PlcReadingEvent event) {
        String redisKey = buildRedisKey(event.source(), event.tagName());
        String newValue = normalizeValue(event.valueAsString());

        Optional<PlcReadingEvent> existingOpt = repository.findByTag(redisKey);

        boolean shouldPublish;

        if (existingOpt.isEmpty()) {
            shouldPublish = true;
        } else {
        	PlcReadingEvent existing = existingOpt.get();
            shouldPublish = !Objects.equals(existing.valueAsString(), newValue);
        }

        if (shouldPublish) {
        	repository.save(redisKey, event);

        	messagingTemplate.convertAndSend("/topic/plc-updates", event);
        }
    }

    private String buildRedisKey(String source, String tagName) {
        return "plc:%s:%s".formatted(source, tagName);
    }

    private String normalizeValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}