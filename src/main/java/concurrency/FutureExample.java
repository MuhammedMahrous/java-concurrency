package concurrency;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class FutureExample {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> future = executorService.submit(() -> {
            try {
                log.info("I'm going to do some async work....");
                Thread.sleep(2000);
                log.info("Finished doing my work..");
                return "Some Async Response";
            } catch (InterruptedException e) {
                log.error("e -> {}", e);
                return "Error";
            }
        });

        log.info("I think I'll do something else till I get my Async response");
        Thread.sleep(1000);
        log.info("Now I have nothing else to do so I'll block for my response");
        String response = future.get();
        log.info("I finally got the response '{}'", response);
        executorService.shutdown();
    }
}
