package concurrency;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Locks makes your program safe against Data Race: multiple threads overlap each others operations
 * causing false calculations.
 * Race Conditions on the other hand can occur even when using Locks, and it happens when
 * the ORDER of operations isn't right for your business logic, so Barriers help sync
 * the Order of those operations.
 */

@Slf4j
public class Barrier {
    private static int number = 0;

    /**
     * To demonstrate the usage of barriers We create two types of threads added and multiplier
     * we'll start the application by running 10 adder (by one) threads and so anticipating number variable
     * to be 10 after they finish, then we'll run 3 multiplier (by two) threads anticipating a final
     * result of 80
     *
     * @param args
     */
    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();
        Lock lock = new ReentrantLock();
        CyclicBarrier cyclicBarrier = new CyclicBarrier(13);
        for (int i = 0; i < 10; i++) {
            Adder adder = new Adder(cyclicBarrier, lock);
            threads.add(adder);
            adder.start();
        }

        for (int i = 0; i < 3; i++) {
            Multiplier multiplier = new Multiplier(cyclicBarrier, lock);
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
        private CyclicBarrier cyclicBarrier;
        private Lock lock;

        @Override
        public void run() {
            try {
                lock.lock();
                number++;
            } finally {
                lock.unlock();
            }
            try {
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }

    @Slf4j
    @AllArgsConstructor
    private static class Multiplier extends Thread {
        private CyclicBarrier cyclicBarrier;
        private Lock lock;

        @Override
        public void run() {
            try {
                cyclicBarrier.await();
                lock.lock();
                number *= 2;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }

        }
    }
}
