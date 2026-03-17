package com.plc.persist.entities;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(
    name = "plc_reading",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_kafka_message",
        columnNames = {"kafka_topic", "kafka_partition", "kafka_offset"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlcReadingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String source;
    private String tagName;
    private String address;
    private String dataType;
    private String valueAsString;

    private Instant eventTime;

    private String quality;

    private Instant receivedTime;

    private String kafkaTopic;
    private Integer kafkaPartition;
    private Long kafkaOffset;

    @PrePersist
    public void prePersist() {
        if (receivedTime == null) {
            receivedTime = Instant.now();
        }
    }
}