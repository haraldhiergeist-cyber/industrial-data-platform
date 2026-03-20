package com.plc.query.api.rest;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.industrial.contracts.event.PlcReadingEvent;
import com.plc.query.application.current.CurrentValueService;

@RestController
@RequestMapping("/api/plc")
public class PlcQueryController {

    private final CurrentValueService service;

    public PlcQueryController(CurrentValueService service) {
        this.service = service;
    }

    @GetMapping("/{tag}")
    public ResponseEntity<PlcReadingEvent> get(@PathVariable String tag) {
    	Optional<PlcReadingEvent> reading = service.getLatest(tag);

        if (reading.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(reading.get());
    }
    
    @PostMapping
    public ResponseEntity<PlcReadingEvent> set(@RequestBody PlcReadingEvent reading) {
    	PlcReadingEvent saved = service.save(reading);
        return ResponseEntity.ok(saved);
    }
    
    @GetMapping
    public ResponseEntity<List<PlcReadingEvent>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
    
    @DeleteMapping("/{tag}")
    public ResponseEntity<Void> delete(@PathVariable String tag) {
        service.delete(tag);
        return ResponseEntity.noContent().build();
    }    
}