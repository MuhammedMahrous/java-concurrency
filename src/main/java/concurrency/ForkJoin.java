package concurrency;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

@Slf4j
public class ForkJoin {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        long end = System.currentTimeMillis();
        long sum = forkJoinPool.submit(new RecursiveSum(0, 1000_000_000)).get();
        long asyncTime = end - start;
        log.info("Async Sum = {} in {} ms", sum, asyncTime);
        forkJoinPool.shutdown();

        start = System.currentTimeMillis();
        sum = 0;
        for (int i = 0; i <= 1000_000_000; i++) {
            sum += i;
        }
        end = System.currentTimeMillis();
        long syncTime = end - start;
        log.info("Sync Sum = {} in {} ms", sum, syncTime);

    }

    @AllArgsConstructor
    public static class RecursiveSum extends RecursiveTask<Long> {
        private int start;
        private int end;

        @Override
        protected Long compute() {
            long sum = 0;
            if (end - start < 100_000) {
                for (int i = start; i <= end; i++) {
                    sum += i;
                }
            } else {
                int mid = (end + start) / 2;
                RecursiveSum left = new RecursiveSum(start, mid);
                RecursiveSum right = new RecursiveSum(mid + 1, end);
                left.fork();
                right.fork();
                sum = left.join() + right.join();
            }
            return sum;
        }
    }

}
