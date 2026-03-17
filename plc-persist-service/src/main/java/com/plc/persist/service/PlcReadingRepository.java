package com.plc.persist.service;

import com.plc.persist.entities.PlcReadingEntity;
import com.plc.persist.service.PlcReadingRepository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PlcReadingRepository extends JpaRepository<PlcReadingEntity, Long> {
	 Optional<PlcReadingEntity> findByKafkaTopicAndKafkaPartitionAndKafkaOffset(
	            String kafkaTopic,
	            Integer kafkaPartition,
	            Long kafkaOffset
	    );
}