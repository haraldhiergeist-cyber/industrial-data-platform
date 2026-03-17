package com.plc.persist.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.industrial.contracts.event.PlcReadingEvent;
import com.plc.persist.entities.PlcReadingEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlcReadingPersistenceService {

    private final PlcReadingMapper mapper;
    private final PlcReadingRepository repository;

    @Transactional
    public PersistResult persist(
            PlcReadingEvent event,
            String kafkaTopic,
            Integer kafkaPartition,
            Long kafkaOffset) {

        boolean alreadyProcessed = repository
                .findByKafkaTopicAndKafkaPartitionAndKafkaOffset(
                        kafkaTopic, kafkaPartition, kafkaOffset)
                .isPresent();

        if (alreadyProcessed) {
            return PersistResult.DUPLICATE;
        }

        PlcReadingEntity entity = mapper.toEntity(event, kafkaTopic, kafkaPartition, kafkaOffset);
        repository.save(entity);

        return PersistResult.PERSISTED;
    }

    public enum PersistResult {
        PERSISTED,
        DUPLICATE
    }
}