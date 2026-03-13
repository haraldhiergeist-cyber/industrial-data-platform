package com.plc.collector;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Map;

import static org.mockito.Mockito.when;

@CamelSpringBootTest
@SpringBootTest
class PlcRouteTest {

    @Autowired
    CamelContext camelContext;

    @MockitoBean("plcReader")
    PlcReader plcReader;
    
    @Autowired
    ProducerTemplate template;

   

    @Test
    void routeStarts() throws Exception {

        when(plcReader.readAll()).thenReturn(Map.of("temperature", 23));

        AdviceWith.adviceWith(camelContext, "plc-polling", a -> {
            a.replaceFromWith("direct:test");
            a.weaveAddLast().to("mock:result");
        });

        MockEndpoint mock = camelContext.getEndpoint("mock:result", MockEndpoint.class);
        mock.expectedMessageCount(1);
        mock.expectedBodiesReceived(Map.of("temperature", 23));

        template.sendBody("direct:test", null);

        mock.assertIsSatisfied();
    }
}