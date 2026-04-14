package com.plc.query.application.history;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


@RestController
@RequestMapping("/api/history")
public class MeasurementHistoryController {

    private final MeasurementHistoryService measurementHistoryService;

    public MeasurementHistoryController(MeasurementHistoryService measurementHistoryService) {
        this.measurementHistoryService = measurementHistoryService;
    }
    
    @Operation(summary = "Load measurement history")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "History loaded")
    })
    @GetMapping(value = "/measurements", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MeasurementHistoryPoint> measurements(
            @RequestParam("metric") MeasurementMetric metric,
            @RequestParam("range") MeasurementRange range
    ) {
        return measurementHistoryService.loadHistory(metric, range);
    }
}