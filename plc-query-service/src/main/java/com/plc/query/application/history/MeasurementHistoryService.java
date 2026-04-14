package com.plc.query.application.history;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class MeasurementHistoryService {

	PlcReadingRepository plcReadingRepository;
	
	public MeasurementHistoryService(PlcReadingRepository plcReadingRepository)
	{
		this.plcReadingRepository = plcReadingRepository;
	}
	public List<MeasurementHistoryPoint> loadHistory(
	        MeasurementMetric metric,
	        MeasurementRange range
	) {
	    Instant to = Instant.now();
	    Instant from = to.minus(range.getDuration());
	    String tagName = resolveTagName(metric);

	    return plcReadingRepository
	            .findByTagNameAndEventTimeBetweenOrderByEventTimeAsc(tagName, from, to)
	            .stream()
	            .map(entity -> new MeasurementHistoryPoint(
	                    entity.getEventTime(),
	                   Double.valueOf(entity.getValueAsString())
	            ))
	            .toList();
	}
	
	 private String resolveTagName(MeasurementMetric metric) {
	        return switch (metric) {
	            case TEMPERATURE -> "temperature";
	            case PRESSURE -> "pressure";
	            case LEVEL -> "level";
	        };
	    }
}
