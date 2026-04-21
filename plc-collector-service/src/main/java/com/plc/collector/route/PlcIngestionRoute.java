package com.plc.collector.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.springframework.stereotype.Component;

import com.plc.collector.processor.PlcReadingEventProcessor;

@Component
public class PlcIngestionRoute extends RouteBuilder {
	
    private final PlcReadingEventProcessor plcReadingEventProcessor;
    private final JacksonDataFormat plcReadingEventJacksonDataFormat;

    public PlcIngestionRoute(
            PlcReadingEventProcessor plcReadingEventProcessor, JacksonDataFormat plcReadingEventJacksonDataFormat
    ) {
        this.plcReadingEventProcessor = plcReadingEventProcessor;
        this.plcReadingEventJacksonDataFormat = plcReadingEventJacksonDataFormat;
       
    }

    @Override
    public void configure() {
    	
    	 errorHandler(defaultErrorHandler()
                 .maximumRedeliveries(3)
                 .redeliveryDelay(2000));

        onException(Exception.class)
            .handled(true)
            .log("PLC polling failed: ${exception.message}");

        from("timer:plcPoll?fixedRate=true&period={{plc.poll-interval-ms}}")
            .routeId("plc-polling")
            .bean("plcReader", "readAll")
            .split(body())
            .process(plcReadingEventProcessor)
            .marshal(plcReadingEventJacksonDataFormat)
           .log("Publishing to Kafka topic {{app.kafka.topics.plc-raw-readings}}: ${body}")
           .to("kafka:{{app.kafka.topics.plc-raw-readings}}"
                + "?brokers={{spring.kafka.bootstrap-servers}}");

    }
}