package com.plc.query.application.history;

import java.time.Instant;

public record MeasurementHistoryPoint(
        Instant timestamp,
        double value
) {
}