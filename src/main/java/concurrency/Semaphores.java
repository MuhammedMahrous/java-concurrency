package concurrency;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Semaphores {
    public static void main(String[] args) {
        Semaphore electricitySockets = new Semaphore(3);
        for (int i = 0; i < 10; i++) {
            new Semaphores.Phone("Phone" + i, electricitySockets).start();
        }

    }

    @Slf4j
    static class Phone extends Thread {
        public static Queue<String> chargers = new ArrayDeque<>(3);
        private Semaphore semaphore;

        public Phone(String name, Semaphore semaphore) {
            super(name);
            this.semaphore = semaphore;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    log.info("Lets see if there's an available socket...");
                    semaphore.acquire();
                    log.info("Found one !! I'll charge now");
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release();
                }
            }
        }

    }


}
