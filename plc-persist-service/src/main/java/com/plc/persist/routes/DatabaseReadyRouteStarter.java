package com.plc.persist.routes;

import java.sql.Connection;
import java.time.Duration;

import javax.sql.DataSource;

import org.apache.camel.CamelContext;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseReadyRouteStarter implements ApplicationRunner {

    private final DataSource dataSource;
    private final CamelContext camelContext;
    private final Sleeper sleeper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        waitForDatabase();
        camelContext.getRouteController().startRoute("plc-persist-route");
    }

    void waitForDatabase() throws Exception {
        Duration delay = Duration.ofSeconds(5);

        while (true) {
            try (Connection connection = dataSource.getConnection()) {
                if (connection.isValid(2)) {
                    return;
                }
            } catch (Exception ex) {
                log.warn("Database not ready yet. Retrying in {} seconds. Cause: {}",
                        delay.toSeconds(), ex.getMessage());
            }

            sleeper.sleep(delay);
        }
    }
}