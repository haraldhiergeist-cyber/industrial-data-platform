package com.plc.collector;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class PlcRoute extends RouteBuilder {

    @Override
    public void configure() {

        onException(Exception.class)
            .handled(true)
            .log("PLC polling failed: ${exception.message}");

        from("timer:plcPoll?fixedRate=true&period={{plc.poll-interval-ms}}")
            .routeId("plc-polling")
            .bean("plcReader", "readAll")
            .log("PLC data: ${body}");
    }
}