package com.plc.query.application.current;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.industrial.contracts.event.PlcReadingEvent;

@Service
public class CurrentValueService {

    private final CurrentValueRepository repository;

    public CurrentValueService(CurrentValueRepository repository) {
        this.repository = repository;
    }

    public Optional<PlcReadingEvent> getLatest(String tag) {
        return repository.findByTag(tag);
    }
    
    public PlcReadingEvent save(PlcReadingEvent reading) {
        repository.save(repository.buildRedisKey(reading.source(), reading.tagName()), reading);
        return reading;
    }
    
    public List<PlcReadingEvent> getAll() {
        return repository.findAll();
    }
    
    public void delete(String tag) {
        repository.delete(tag);
    }
}