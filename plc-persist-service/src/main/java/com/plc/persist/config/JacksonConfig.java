package com.plc.persist.config;

import org.apache.camel.component.jackson3.JacksonDataFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.industrial.contracts.event.PlcReadingEvent;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class JacksonConfig {

    @Bean
    JacksonDataFormat plcReadingEventJacksonDataFormat() {
        ObjectMapper mapper = JsonMapper.builder()
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();

        JacksonDataFormat format = new JacksonDataFormat();
        format.setObjectMapper(mapper);
        format.setUnmarshalType(PlcReadingEvent.class);
        return format;
    }
}