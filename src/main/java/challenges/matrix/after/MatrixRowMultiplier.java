package challenges.matrix.after;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

@Slf4j
public class MatrixRowMultiplier extends ParallelMatrixMultiplier implements Runnable {
    private CountDownLatch countDownLatch;
    private int start, end;
    private int[][] result;

    public MatrixRowMultiplier(ParallelMatrixMultiplier parallelMatrixMultiplier,
                               int[][] result, int start, int end, CountDownLatch countDownLatch) {
        super(parallelMatrixMultiplier.A, parallelMatrixMultiplier.B);
        this.countDownLatch = countDownLatch;
        this.start = start;
        this.end = end;
        this.result = result;
    }

    @Override
    public void run() {
        long s = System.currentTimeMillis();
        try {
            for (int i = start; i <= end; i++) {
                for (int k = 0; k < numColsB; k++) {
                    int sum = 0;
                    for (int j = 0; j < numColsA; j++) {
                        sum += A[i][j] * B[j][k];
                    }
                    result[i][k] = sum;
                }
            }
        } catch (Exception e) {
            log.error("{}", e);
        } finally {
            countDownLatch.countDown();
            long e = System.currentTimeMillis();
            log.info("Total: {}", e - s);
        }
    }
}
