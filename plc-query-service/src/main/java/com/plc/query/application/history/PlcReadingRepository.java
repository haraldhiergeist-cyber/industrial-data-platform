package com.plc.query.application.history;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PlcReadingRepository extends JpaRepository<PlcReadingEntity, Long> {

    List<PlcReadingEntity> findTop500ByTagNameOrderByEventTimeDesc(String tagName);

}