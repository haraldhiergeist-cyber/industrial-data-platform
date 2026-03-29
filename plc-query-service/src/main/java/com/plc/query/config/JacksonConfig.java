package com.plc.query.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.industrial.contracts.event.PlcReadingEvent;

@Configuration
public class JacksonConfig {

    @Bean
    ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Bean
    JacksonDataFormat plcReadingEventJacksonDataFormat(ObjectMapper objectMapper) {
        JacksonDataFormat format = new JacksonDataFormat();
        format.setObjectMapper(objectMapper);
        format.setUnmarshalType(PlcReadingEvent.class);
        return format;
    }
}