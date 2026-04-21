package com.plc.persist.routes;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson3.JacksonDataFormat;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.component.kafka.consumer.KafkaManualCommit;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import com.example.industrial.contracts.event.PlcReadingEvent;
import com.plc.persist.service.PlcReadingPersistenceService;
import com.plc.persist.service.PlcReadingPersistenceService.PersistResult;

import jakarta.annotation.PostConstruct;

@Component
public class PlcPersistRoute extends RouteBuilder {

	private final JacksonDataFormat plcReadingEventJacksonDataFormat;
    private final PlcReadingPersistenceService persistenceService;

    public PlcPersistRoute(
            JacksonDataFormat plcReadingEventJacksonDataFormat,
            PlcReadingPersistenceService persistenceService) {
        this.plcReadingEventJacksonDataFormat = plcReadingEventJacksonDataFormat;
        this.persistenceService = persistenceService;
    }
    
    private Map<String, PlcReadingEvent> eventCache;
    
    @PostConstruct
    public void init()
    {
    	eventCache = new HashMap<String, PlcReadingEvent>();
    }

    @Override
    public void configure() {

        onException(DataAccessException.class)
        	.maximumRedeliveries(-1) 
        	.redeliveryDelay(2000) // Each exception doubles the waiting time until the database is ready.
        	.backOffMultiplier(2)  // Starts with 2 seconds and ends at 60 seconds
        	.maximumRedeliveryDelay(60000); 

        from("kafka:{{app.kafka.topics.plc-raw-readings}}"
                + "?brokers={{spring.kafka.bootstrap-servers}}"
                + "&groupId={{spring.kafka.consumer.group-id}}"
                + "&autoCommitEnable=false"
                + "&allowManualCommit=true"
                + "&breakOnFirstError=true"
                + "&autoOffsetReset=earliest")
            .routeId("plc-persist-route")

            .autoStartup(false) // Must be started from an external source -> DatabaseReadyRouteStarter
            
            .unmarshal(plcReadingEventJacksonDataFormat)

            .process(this::persistReading)

            .process(this::commitOffset)

            .log(LoggingLevel.DEBUG, "Kafka offset committed successfully");
    }

    private void persistReading(Exchange exchange) {
        PlcReadingEvent event = exchange.getIn().getBody(PlcReadingEvent.class);

        String topic = exchange.getIn().getHeader(KafkaConstants.TOPIC, String.class);
        Integer partition = exchange.getIn().getHeader(KafkaConstants.PARTITION, Integer.class);
        Long offset = exchange.getIn().getHeader(KafkaConstants.OFFSET, Long.class);

        PlcReadingEvent oldEvent = this.searchInCache(event);
        if(hasMeaningfulChange(oldEvent, event))
        {	
	        PersistResult result = persistenceService.persist(event, topic, partition, offset);
	        putToCache(event);
	        if (result == PersistResult.PERSISTED) {
	            log.info("PLC reading persisted: topic={}, partition={}, offset={}", topic, partition, offset);
	        } else {
	            log.info("PLC reading already processed, skipping duplicate: topic={}, partition={}, offset={}",
	                    topic, partition, offset);
	        }
        }
    }

    private void commitOffset(Exchange exchange) {
        KafkaManualCommit manualCommit =
                exchange.getIn().getHeader(KafkaConstants.MANUAL_COMMIT, KafkaManualCommit.class);

        if (manualCommit == null) {
            throw new IllegalStateException("Kafka manual commit header is missing");
        }

        manualCommit.commit();
    }
    
    private boolean hasMeaningfulChange(PlcReadingEvent oldValue, PlcReadingEvent newValue) {
        if (oldValue == null) {
            return true;
        }
        return !Objects.equals(oldValue.valueAsString(), newValue.valueAsString());
    }
    
    private PlcReadingEvent searchInCache(PlcReadingEvent event)
    {
    	return eventCache.get(event.tagName());
    }
    
    private void putToCache(PlcReadingEvent event)
    {
    	eventCache.put(event.tagName(), event);
    }
}