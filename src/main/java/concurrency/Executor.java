package concurrency;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class Executor {
    public static void main(String[] args) {
        int numOfCPUs = Runtime.getRuntime().availableProcessors();
        log.info("You Have {} logical CPUs", numOfCPUs);
        ExecutorService executorService = Executors.newFixedThreadPool(numOfCPUs);
        for (int i = 0; i < 50; i++) {
            executorService.submit(() -> log.info("Doing some work...."));
        }
        executorService.shutdown();
    }
}
