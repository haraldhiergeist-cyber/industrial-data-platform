package com.plc.query.application.history;

import java.time.Instant;
import java.util.List;


public interface MeasurementHistoryRepository {
    List<MeasurementHistoryPoint> findHistory(MeasurementMetric metric, Instant from, Instant to);
}