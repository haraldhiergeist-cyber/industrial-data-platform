package com.plc.query.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.example.industrial.contracts.event.PlcReadingEvent;

import org.springframework.beans.factory.annotation.Value;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id:plc-query-service}")
    private String groupId;

    @Bean
    ConsumerFactory<String, PlcReadingEvent> plcReadingConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        JsonDeserializer<PlcReadingEvent> jsonDeserializer =
                new JsonDeserializer<>(PlcReadingEvent.class);
        jsonDeserializer.addTrustedPackages("com.example.industrial.contracts.event");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                jsonDeserializer
        );
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, PlcReadingEvent> plcReadingKafkaListenerContainerFactory(
            ConsumerFactory<String, PlcReadingEvent> plcReadingConsumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, PlcReadingEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(plcReadingConsumerFactory);
        return factory;
    }
}