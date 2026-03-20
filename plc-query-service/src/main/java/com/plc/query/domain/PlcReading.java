package com.plc.query.domain;

public record PlcReading(
	    String tagName,
	    String address,
	    Object value,
	    long timestamp
	) {
}