package com.plc.persist.routes;

import java.time.Duration;

public interface Sleeper {
    void sleep(Duration duration) throws InterruptedException;
}