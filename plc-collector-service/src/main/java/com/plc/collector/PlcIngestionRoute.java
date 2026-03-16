package com.plc.collector;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import com.example.industrial.contracts.topic.Topics;
import com.plc.collector.processor.PlcReadingEventProcessor;
import com.plc.collector.processor.RedisStateProcessor;

@Component
public class PlcIngestionRoute extends RouteBuilder {
	
    private final PlcReadingEventProcessor plcReadingEventProcessor;
    private final RedisStateProcessor redisStateProcessor;

    public PlcIngestionRoute(
            PlcReadingEventProcessor plcReadingEventProcessor,
            RedisStateProcessor redisStateProcessor
    ) {
        this.plcReadingEventProcessor = plcReadingEventProcessor;
        this.redisStateProcessor = redisStateProcessor;
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
            .multicast()
                .to("direct:publish-kafka", "direct:update-redis");
        
        from("direct:publish-kafka")
           .routeId("publish-kafka")
           .marshal().json()
           .log("Publishing to Kafka topic " + Topics.PLC_RAW_READINGS + ": ${body}")
           .to("kafka:" + Topics.PLC_RAW_READINGS
                + "?brokers={{spring.kafka.bootstrap-servers}}");

        from("direct:update-redis")
           .routeId("update-redis")
           . process(redisStateProcessor)
           .log("Redis state updated under key ${header.redis.key}");
    }
}