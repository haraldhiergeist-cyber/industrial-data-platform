package com.plc.collector;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(properties = {
        "camel.springboot.auto-startup=false",
        "spring.kafka.bootstrap-servers=localhost:9092",
        "plc.poll-interval-ms=1000",
        "plc.connection-string=s7://127.0.0.1?controller-type=S7_300",
        "plc.auto-reconnect=true",
        "plc.source=plc-simulator-1"
})
class PlcCollectorServiceApplicationTest {

    @Test
    void shouldMapReadingToEventBeforePublishing() {
    }
}
