package com.plc.query.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.component.kafka.consumer.KafkaManualCommit;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import com.example.industrial.contracts.event.PlcReadingEvent;
import com.plc.query.application.current.PlcStateUpdateService;


@Component
public class PlcConsumerRoute extends RouteBuilder {

	private final JacksonDataFormat plcReadingEventJacksonDataFormat;
    private final PlcStateUpdateService plcStateUpdateService;

    public PlcConsumerRoute(
            JacksonDataFormat plcReadingEventJacksonDataFormat, PlcStateUpdateService plcStateUpdateService) {
        this.plcReadingEventJacksonDataFormat = plcReadingEventJacksonDataFormat;
        this.plcStateUpdateService = plcStateUpdateService;
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
            .unmarshal(plcReadingEventJacksonDataFormat)
            .process(this::persistReading)

            .log(LoggingLevel.DEBUG, "Kafka offset committed successfully");
    }

    private void persistReading(Exchange exchange) {
        PlcReadingEvent event = exchange.getIn().getBody(PlcReadingEvent.class);

        plcStateUpdateService.handleIncomingReading(event); 
   }

}