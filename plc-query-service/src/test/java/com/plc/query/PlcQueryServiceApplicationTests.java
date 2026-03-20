package com.plc.query;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.kafka.bootstrap-servers=localhost:9092",
        "spring.data.redis.host=localhost",
        "spring.data.redis.port=6379"
})
class PlcQueryServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
