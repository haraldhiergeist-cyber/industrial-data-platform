package com.plc.collector.route;

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

import static org.mockito.Mockito.when;

@CamelSpringBootTest
@SpringBootTest(properties = {
        "camel.springboot.auto-startup=false",
        "spring.kafka.bootstrap-servers=localhost:9092",
        "spring.data.redis.host=localhost",
        "spring.data.redis.port=6379"}
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class PlcPollingRouteSplitTest {

    @Autowired
    CamelContext camelContext;

    @Autowired
    ProducerTemplate template;

    @MockitoBean
    PlcReader plcReader;

    @EndpointInject("mock:kafka")
    MockEndpoint kafkaMock;

    @Test
    void shouldSplitReadingsIntoSingleMessages() throws Exception {
        PlcReader.PlcReading reading1 =
                new PlcReader.PlcReading("plc-simulator-1", "temperature", "%DB90.DBW0:INT", 23);

        PlcReader.PlcReading reading2 =
                new PlcReader.PlcReading("plc-simulator-1", "pressure", "%DB90.DBW2:INT", 7);

        when(plcReader.readAll()).thenReturn(List.of(reading1, reading2));

        AdviceWith.adviceWith(camelContext, "plc-polling", a -> {
            a.replaceFromWith("direct:test");
            a.weaveByToUri("direct:publish-kafka").replace().to("mock:kafka");
            a.weaveByToUri("direct:update-redis").remove();
        });

        camelContext.start();

        kafkaMock.expectedMessageCount(2);

        template.sendBody("direct:test", null);

        kafkaMock.assertIsSatisfied();
    }
}