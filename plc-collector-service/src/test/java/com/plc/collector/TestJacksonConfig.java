package com.plc.collector;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import com.fasterxml.jackson.databind.ObjectMapper;

@TestConfiguration
public class TestJacksonConfig {

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
