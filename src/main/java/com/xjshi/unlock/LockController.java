package com.xjshi.unlock;

import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

@RestController
public class LockController {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Semaphore semaphore = new Semaphore(0);
    private final String defaultUsername = "Jack";

    @RequestMapping("/status")
    public String status() {
        return String.valueOf(semaphore.availablePermits());
    }

    @RequestMapping("/lock/{username}")
    public String lock(@PathVariable String username) {
        if (username.isEmpty()) {
            username = defaultUsername;
        }

        logger.info("Lock " + username);

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            logger.error(e.getLocalizedMessage());
        }

        return username + " locked. " + semaphore.availablePermits();
    }

    @RequestMapping("/unlock/{username}")
    public String unlock(@PathVariable String username) {
        if (username.isEmpty()) {
            username = defaultUsername;
        }

        logger.info("Unlock " + username);

        semaphore.release();

        return username + " unlocked. " + semaphore.availablePermits();
    }
}
