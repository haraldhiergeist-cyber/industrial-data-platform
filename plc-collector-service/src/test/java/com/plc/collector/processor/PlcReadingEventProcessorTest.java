package com.plc.collector.processor;

import com.example.industrial.contracts.event.PlcReadingEvent;
import com.example.industrial.contracts.model.Quality;
import com.plc.collector.PlcReader.PlcReading;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlcReadingEventProcessorTest {

    private final PlcReadingEventProcessor processor = new PlcReadingEventProcessor();

    @Test
    void shouldMapPlcReadingToPlcReadingEvent() throws Exception {
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());

        PlcReading reading = new PlcReading(
                "plc-simulator-1",
                "temperature",
                "%DB90.DBW0:INT",
                23
        );

        exchange.getMessage().setBody(reading);

        processor.process(exchange);

        PlcReadingEvent event = exchange.getMessage().getBody(PlcReadingEvent.class);

        assertNotNull(event);
        assertEquals("plc-simulator-1", event.source());
        assertEquals("temperature", event.tagName());
        assertEquals("%DB90.DBW0:INT", event.address());
        assertEquals("Integer", event.dataType());
        assertEquals("23", event.valueAsString());
        assertEquals(Quality.GOOD, event.quality());
        assertNotNull(event.timestamp());
    }

    @Test
    void shouldHandleNullValue() throws Exception {
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());

        PlcReading reading = new PlcReading(
                "plc-simulator-1",
                "temperature",
                "%DB90.DBW0:INT",
                null
        );

        exchange.getMessage().setBody(reading);

        processor.process(exchange);

        PlcReadingEvent event = exchange.getMessage().getBody(PlcReadingEvent.class);

        assertNotNull(event);
        assertEquals("plc-simulator-1", event.source());
        assertEquals("temperature", event.tagName());
        assertEquals("%DB90.DBW0:INT", event.address());
        assertEquals("UNKNOWN", event.dataType());
        assertNull(event.valueAsString());
        assertEquals(Quality.GOOD, event.quality());
        assertNotNull(event.timestamp());
    }
}