package concurrency;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class ReadWriteLocks {

    public static void main(String[] args) {
        ReentrantReadWriteLock myLock = new ReentrantReadWriteLock();
        for (int i = 0; i < 10; i++) {
            new Reader(myLock.readLock()).start();
        }

        for (int i = 0; i < 3; i++) {
            new Writer(myLock.writeLock()).start();
        }
    }

    @Slf4j
    @AllArgsConstructor
    static class Reader extends Thread {
        private ReentrantReadWriteLock.ReadLock readLock;

        @Override
        public void run() {
            long time = 0;
            while (true) {
                try {
                    time = System.currentTimeMillis();
                    readLock.lock();
                    time = System.currentTimeMillis() - time;
                    log.info("R | Waited {} ms to acquire a read lock", time);
                } finally {
                    readLock.unlock();
                }
            }
        }
    }

    @Slf4j
    @AllArgsConstructor
    static class Writer extends Thread {
        private ReentrantReadWriteLock.WriteLock writeLock;

        @Override
        public void run() {
            long time = 0;
            while (true) {
                Random random = new Random();
                if (random.nextInt() > 0.6) {
                    try {
                        time = System.currentTimeMillis();
                        writeLock.lock();
                        time = System.currentTimeMillis() - time;
                        log.info("W | Waited {} ms to acquire a write lock", time);
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        writeLock.unlock();
                    }
                }
            }
        }
    }
}
