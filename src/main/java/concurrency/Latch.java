package concurrency;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Look @{@link Barrier} this is the same logic but with @{@link CountDownLatch} instead
 */
@Slf4j
public class Latch {
    private static int number = 0;

    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();
        Lock lock = new ReentrantLock();
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            Adder adder = new Adder(countDownLatch, lock);
            threads.add(adder);
            adder.start();
        }

        for (int i = 0; i < 3; i++) {
            Multiplier multiplier = new Multiplier(countDownLatch, lock);
            threads.add(multiplier);
            multiplier.start();
        }

        // Make sure everyone is finished before printing the final number
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                log.error("e: {}", e);
            }
        });


        log.info("Final number -> {}", number);
    }

    @Slf4j
    @AllArgsConstructor
    private static class Adder extends Thread {
        private CountDownLatch countDownLatch;
        private Lock lock;

        @Override
        public void run() {
            try {
                lock.lock();
                number++;
                countDownLatch.countDown();
            } finally {
                lock.unlock();
            }
        }
    }

    @Slf4j
    @AllArgsConstructor
    private static class Multiplier extends Thread {
        private CountDownLatch countDownLatch;
        private Lock lock;

        @Override
        public void run() {
            try {
                countDownLatch.await();
                lock.lock();
                number *= 2;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }

        }
    }

}
