package com.plc.query.api.kafka;


import com.example.industrial.contracts.event.PlcReadingEvent;
import com.plc.query.application.current.PlcStateUpdateService;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PlcReadingEventConsumer {

    private final PlcStateUpdateService plcStateUpdateService;

    public PlcReadingEventConsumer(PlcStateUpdateService plcStateUpdateService) {
        this.plcStateUpdateService = plcStateUpdateService;
    }

    @KafkaListener(
            topics = "${app.kafka.topics.plc-raw-readings}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "plcReadingKafkaListenerContainerFactory"
    )
    public void consume(PlcReadingEvent event) {
        plcStateUpdateService.handleIncomingReading(event);
    }
}