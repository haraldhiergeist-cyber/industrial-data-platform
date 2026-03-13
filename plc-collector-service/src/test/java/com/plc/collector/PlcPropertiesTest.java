package com.plc.collector;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PlcPropertiesTest {

    @Autowired
    PlcProperties properties;

    @Test
    void propertiesLoaded() {
        assertThat(properties.getConnectionString()).isNotNull();
        assertThat(properties.getTags()).isNotEmpty();
    }
}