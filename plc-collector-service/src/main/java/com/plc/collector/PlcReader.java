package com.plc.collector;

import jakarta.annotation.PreDestroy;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.PlcDriverManager;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component("plcReader")
public class PlcReader {

    private final PlcProperties properties;
    private PlcConnection connection;

    public PlcReader(PlcProperties properties) {
        this.properties = properties;
    }

    public synchronized Map<String, Object> readAll() throws Exception {
        ensureConnected();

        var builder = connection.readRequestBuilder();
        for (PlcProperties.TagConfig tag : properties.getTags()) {
            builder.addTagAddress(tag.getName(), tag.getAddress());
        }

        PlcReadResponse response = builder.build()
                .execute()
                .get(5, TimeUnit.SECONDS);

        Map<String, Object> result = new LinkedHashMap<>();
        for (PlcProperties.TagConfig tag : properties.getTags()) {
            result.put(tag.getName(), response.getObject(tag.getName()));
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
}