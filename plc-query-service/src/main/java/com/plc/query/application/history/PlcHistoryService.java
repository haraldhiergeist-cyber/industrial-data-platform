package com.plc.query.application.history;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.industrial.contracts.historic.HistoryPoint;

@Service
public class PlcHistoryService {

    private final PlcReadingRepository repository;

    public PlcHistoryService(PlcReadingRepository repository) {
        this.repository = repository;
    }

    public List<HistoryPoint> getTemperatureHistory() {
        return repository.findTop500ByTagNameOrderByEventTimeDesc("temperature")
                .stream()
                .map(e -> new HistoryPoint(e.getEventTime(), e.getValueAsString()))
                .toList();
    }
}