package com.plc.collector.processor;

import com.example.industrial.contracts.event.PlcReadingEvent;
import com.example.industrial.contracts.model.Quality;
import com.plc.collector.service.PlcReader.PlcReading;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class PlcReadingEventProcessor implements Processor {

    @Override
    public void process(Exchange exchange) {
        PlcReading reading = exchange.getMessage().getBody(PlcReading.class);

        Object rawValue = reading.value();
        String valueAsString = rawValue != null ? String.valueOf(rawValue) : null;
        String dataType = rawValue != null ? rawValue.getClass().getSimpleName() : "UNKNOWN";

        PlcReadingEvent event = new PlcReadingEvent(
                reading.source(),
                reading.tagName(),
                reading.address(),
                dataType,
                valueAsString,
                Instant.now(),
                Quality.GOOD
        );

        exchange.getMessage().setBody(event);
    }
}
