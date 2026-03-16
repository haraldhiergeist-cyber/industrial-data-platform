package com.plc.collector;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        classes = PlcPropertiesTest.TestConfig.class,
        properties = {
                "plc.connection-string=s7://127.0.0.1?controller-type=S7_300",
                "plc.poll-interval-ms=1000",
                "plc.auto-reconnect=true",
                "plc.source=plc-simulator-1",
                "plc.tags[0].name=temperature",
                "plc.tags[0].address=%DB90.DBW0:INT",
                "plc.tags[1].name=pressure",
                "plc.tags[1].address=%DB90.DBW2:INT",
                "plc.tags[2].name=level",
                "plc.tags[2].address=%DB90.DBW4:INT"
        }
)
class PlcPropertiesTest {

    @Autowired
    PlcProperties properties;

    @Test
    void shouldBindProperties() {
        assertNotNull(properties);

        assertEquals("s7://127.0.0.1?controller-type=S7_300", properties.getConnectionString());
        assertEquals(1000, properties.getPollIntervalMs());
        assertTrue(properties.isAutoReconnect());
        assertEquals("plc-simulator-1", properties.getSource());

        assertNotNull(properties.getTags());
        assertEquals(3, properties.getTags().size());

        assertEquals("temperature", properties.getTags().get(0).getName());
        assertEquals("%DB90.DBW0:INT", properties.getTags().get(0).getAddress());

        assertEquals("pressure", properties.getTags().get(1).getName());
        assertEquals("%DB90.DBW2:INT", properties.getTags().get(1).getAddress());

        assertEquals("level", properties.getTags().get(2).getName());
        assertEquals("%DB90.DBW4:INT", properties.getTags().get(2).getAddress());
    }

    @Configuration
    @EnableConfigurationProperties(PlcProperties.class)
    static class TestConfig {
    }
}