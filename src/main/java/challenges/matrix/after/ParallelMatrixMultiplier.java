package challenges.matrix.after;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* parallel implementation of matrix multiplication */
@Slf4j
public class ParallelMatrixMultiplier {

    protected int[][] A, B;
    protected int numRowsA, numColsA, numRowsB, numColsB;

    public ParallelMatrixMultiplier(int[][] A, int[][] B) {
        this.A = A;
        this.B = B;
        this.numRowsA = A.length;
        this.numColsA = A[0].length;
        this.numRowsB = B.length;
        this.numColsB = B[0].length;
        if (numColsA != numRowsB)
            throw new Error(String.format("Invalid dimensions; Cannot multiply %dx%d*%dx%d\n",
                    numRowsA, numRowsB, numColsA, numColsB));
    }

    /* returns matrix product C = AB */
    public int[][] computeProduct() throws InterruptedException {
        int[][] result = new int[numRowsA][numColsB];

        // Divide numRowsA to thread count
        int threadCount = calculateThreadCount(numRowsA);
        int partitionsCount = numRowsA / threadCount;
        int partitionsReminder = numRowsA % threadCount;
        log.info("Will use {} threads with {} partitionsCount and {} partitionsReminder",
                threadCount, partitionsCount, partitionsReminder);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        long s = System.currentTimeMillis();
        for (int i = 0; i < threadCount; i++) {
            int start = i * partitionsCount;
            int end = start + partitionsCount - 1;
            if (i == threadCount - 1) {
                log.debug("I am the last thread so special I have to set my end to numRowsA = {}", numRowsA);
                end = numRowsA - 1;
            }
            log.debug("Creating thread #{} with start {} and end {}", i, start, end);

            executorService.submit(new MatrixRowMultiplier(this, result, start, end, countDownLatch));
        }
        countDownLatch.await();
        long e = System.currentTimeMillis();
        log.info("Total: {}", e - s);
        executorService.shutdown();
        return result;
    }

    private int calculateThreadCount(int numRowsA) {
        int cpus = Runtime.getRuntime().availableProcessors();
        return Math.min(numRowsA, cpus);
    }

}
