package ru.checkdev.auth.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class Retry {

    private final int retries;
    private final long delay;

    public interface Act<T> {
        T exec() throws Exception;
    }

    public <R> R exec(Act<R> act, R defVal) {
        int i = 0;
        do {
            i++;
            try {
                return act.exec();
            } catch (Exception e) {
                log.error("Attempt {} failed: {}", i, e.getMessage());
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        } while (i < retries);
        return defVal;
    }
}
