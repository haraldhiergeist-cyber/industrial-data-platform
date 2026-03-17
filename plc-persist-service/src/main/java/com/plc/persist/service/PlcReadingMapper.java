package com.plc.persist.service;

import com.example.industrial.contracts.event.PlcReadingEvent;
import com.plc.persist.entities.PlcReadingEntity;
import org.springframework.stereotype.Component;

@Component
public class PlcReadingMapper {

	 public PlcReadingEntity toEntity(
	            PlcReadingEvent event,
	            String topic,
	            int partition,
	            long offset) {

	        return PlcReadingEntity.builder()
	                .source(event.source())
	                .tagName(event.tagName())
	                .address(event.address())
	                .dataType(event.dataType())
	                .valueAsString(event.valueAsString())
	                .eventTime(event.timestamp())
	                .quality(event.quality() != null ? event.quality().name() : "UNKNOWN")
	                .kafkaTopic(topic)
	                .kafkaPartition(partition)
	                .kafkaOffset(offset)
	                .build();
	 }
}