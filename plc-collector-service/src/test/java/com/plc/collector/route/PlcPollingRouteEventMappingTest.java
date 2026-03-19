package com.plc.collector.route;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.apache.camel.builder.AdviceWith;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.plc.collector.service.PlcReader;
import com.plc.collector.service.PlcReader.PlcReading;
import com.plc.collector.processor.PlcReadingEventProcessor;

@CamelSpringBootTest
@SpringBootTest
@UseAdviceWith
class PlcPollingRouteEventMappingTest {

    @Autowired
    CamelContext camelContext;

    @Autowired
    ProducerTemplate producerTemplate;

    @MockitoBean(name = "plcReader")
    PlcReader plcReader;

    @MockitoBean
    PlcReadingEventProcessor plcReadingEventProcessor;

    @BeforeEach
    void setup() throws Exception {
        AdviceWith.adviceWith(camelContext, "plc-polling", a -> {
            a.replaceFromWith("direct:testStart");
            a.mockEndpointsAndSkip("kafka:*");
        });

        camelContext.start();

        // Processor im Test neutral machen
        doAnswer(invocation -> null).when(plcReadingEventProcessor).process(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldReadAllSplitMarshalAndSendEachReadingToKafka() throws Exception {
        List<PlcReading> readings = List.of(
            new PlcReading("PLC", "temperature", "%DB90.DBW0:INT", 21),
            new PlcReading("PLC", "pressure", "%DB90.DBW2:INT", 7),
            new PlcReading("PLC", "level", "%DB90.DBW4:INT", 42)
        );

        when(plcReader.readAll()).thenReturn(readings);

        MockEndpoint mockKafka =
            camelContext.getEndpoint("mock:kafka:plc.raw.readings", MockEndpoint.class);

        mockKafka.expectedMessageCount(3);
        mockKafka.message(0).body(String.class).contains("temperature");
        mockKafka.message(1).body(String.class).contains("pressure");
        mockKafka.message(2).body(String.class).contains("level");

        producerTemplate.sendBody("direct:testStart", null);

        mockKafka.assertIsSatisfied();
    }
}