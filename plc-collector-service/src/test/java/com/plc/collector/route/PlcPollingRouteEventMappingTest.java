package com.plc.collector.route;

import com.example.industrial.contracts.event.PlcReadingEvent;
import com.example.industrial.contracts.model.Quality;
import com.plc.collector.PlcCollectorServiceApplication;
import com.plc.collector.PlcReader;
import com.plc.collector.TestJacksonConfig;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@CamelSpringBootTest
@SpringBootTest(properties = {
        "camel.springboot.auto-startup=false",
        "spring.kafka.bootstrap-servers=localhost:9092",
        "spring.data.redis.host=localhost",
        "spring.data.redis.port=6379"}
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class PlcPollingRouteEventMappingTest {

    @Autowired
    CamelContext camelContext;

    @Autowired
    ProducerTemplate template;

    @MockitoBean
    PlcReader plcReader;

    @EndpointInject("mock:kafka")
    MockEndpoint kafkaMock;

    @Test
    void shouldMapReadingToEventBeforePublishing() throws Exception {
        PlcReader.PlcReading reading =
                new PlcReader.PlcReading("plc-simulator-1", "temperature", "%DB90.DBW0:INT", 23);

        when(plcReader.readAll()).thenReturn(List.of(reading));

        AdviceWith.adviceWith(camelContext, "plc-polling", a -> {
            a.replaceFromWith("direct:test");
            a.weaveByToUri("direct:publish-kafka").replace().to("mock:kafka");
            a.weaveByToUri("direct:update-redis").remove();
        });

        camelContext.start();

        kafkaMock.expectedMessageCount(1);

        template.sendBody("direct:test", null);

        kafkaMock.assertIsSatisfied();

        PlcReadingEvent event = kafkaMock.getExchanges()
                .get(0)
                .getMessage()
                .getBody(PlcReadingEvent.class);

        assertNotNull(event);
        assertEquals("plc-simulator-1", event.source());
        assertEquals("temperature", event.tagName());
        assertEquals("%DB90.DBW0:INT", event.address());
        assertEquals("Integer", event.dataType());
        assertEquals("23", event.valueAsString());
        assertEquals(Quality.GOOD, event.quality());
        assertNotNull(event.timestamp());
    }
}