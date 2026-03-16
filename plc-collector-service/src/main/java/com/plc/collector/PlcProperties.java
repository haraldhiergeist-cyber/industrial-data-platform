package com.plc.collector;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "plc")
public class PlcProperties {

    private String connectionString;
    private String source;
    private long pollIntervalMs = 1000;
    private boolean autoReconnect;
    private List<TagConfig> tags = new ArrayList<>();

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public long getPollIntervalMs() {
        return pollIntervalMs;
    }
    
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setPollIntervalMs(long pollIntervalMs) {
        this.pollIntervalMs = pollIntervalMs;
    }

    public List<TagConfig> getTags() {
        return tags;
    }

    public void setTags(List<TagConfig> tags) {
        this.tags = tags;
    }

    public boolean isAutoReconnect() {
		return autoReconnect;
	}

	public void setAutoReconnect(boolean autoReconnect) {
		this.autoReconnect = autoReconnect;
	}

	public static class TagConfig {
        private String name;
        private String address;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}