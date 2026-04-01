package com.plc.query.api.rest;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.industrial.contracts.historic.HistoryPoint;
import com.plc.query.application.history.PlcHistoryService;

@RestController
@RequestMapping("/api/history")
public class PlcHistoryController {

    private final PlcHistoryService service;

    public PlcHistoryController(PlcHistoryService service) {
        this.service = service;
    }

    @GetMapping("/temperature")
    public List<HistoryPoint> temperature() {
        return service.getTemperatureHistory();
    }
}