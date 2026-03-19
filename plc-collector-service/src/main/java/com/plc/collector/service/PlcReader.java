package com.plc.collector.service;

import jakarta.annotation.PreDestroy;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.PlcDriverManager;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.springframework.stereotype.Component;

import com.plc.collector.PlcProperties;
import com.plc.collector.PlcProperties.TagConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component("plcReader")
public class PlcReader {

    private final PlcProperties properties;
    private PlcConnection connection;

    public PlcReader(PlcProperties properties) {
        this.properties = properties;
    }

    public synchronized List<PlcReading> readAll() throws Exception {

        ensureConnected();

        var builder = connection.readRequestBuilder();

        for (PlcProperties.TagConfig tag : properties.getTags()) {
            builder.addTagAddress(tag.getName(), tag.getAddress());
        }

        PlcReadResponse response = builder.build()
                .execute()
                .get(5, TimeUnit.SECONDS);

        List<PlcReading> result = new ArrayList<>();

        for (PlcProperties.TagConfig tag : properties.getTags()) {

            Object value = response.getObject(tag.getName());

            result.add(new PlcReading(
                    properties.getSource(),
                    tag.getName(),
                    tag.getAddress(),
                    value
            ));
        }

        return result;
    }

    private void ensureConnected() throws Exception {
        if (connection == null || !connection.isConnected()) {
            disconnect();
            connection = PlcDriverManager.getDefault()
                    .getConnectionManager()
                    .getConnection(properties.getConnectionString());
        }
    }

    @PreDestroy
    public synchronized void disconnect() {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception ignored) {
            } finally {
                connection = null;
            }
        }
    }
    
    public record PlcReading(
            String source,
            String tagName,
            String address,
            Object value
    ) {}
}