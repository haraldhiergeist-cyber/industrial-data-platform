package com.plc.collector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(PlcProperties.class)
public class PlcCollectorServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlcCollectorServiceApplication.class, args);
	}

}
