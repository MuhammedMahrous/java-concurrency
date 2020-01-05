package concurrency;


import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class ConditionVariables {

    public static void main(String[] args) {
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        for (int i = 0; i < 10; i++) {
            new Eater("Eater" + i, lock, condition).start();
        }

        new Chief(lock, condition).start();
    }

    @Slf4j
    static class Eater extends Thread {
        private Lock lock;
        private Condition condition;
        public static String food = null;

        public Eater(String name, Lock lock, Condition condition) {
            super(name);
            this.lock = lock;
            this.condition = condition;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    lock.lock();
                    if (food == null) {
                        log.info("Oh No Food, I'll wait");
                        condition.await();
                    }
                    log.info("Consuming food {}", food);
                    food = null;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }

    }

    @Slf4j
    static class Chief extends Thread {
        private Lock lock;
        private Condition condition;

        public Chief(Lock lock, Condition condition) {
            super("Chief");
            this.lock = lock;
            this.condition = condition;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    lock.lock();
                    if (Eater.food == null) {
                        Eater.food = "Hot Backed food";
                        Thread.sleep(3000);
                        log.info("Backed Food {}", Eater.food);
                        condition.signal();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }

    }
}
