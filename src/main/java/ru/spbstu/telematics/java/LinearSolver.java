package ru.spbstu.telematics.java;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LinearSolver {

    private final ExecutorService executorService;

    public LinearSolver(int nThreads) {
        this.executorService = Executors.newFixedThreadPool(nThreads);
    }

    public BigDecimal[] solveParallel(BigDecimal[][] K, BigDecimal[] F, int threadCount)
            throws InterruptedException {
        int n = F.length;
        int threadStep = n / threadCount;
        int from = 0, to;
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        for (int th = 0; th < threadCount; ++th) {
            if (n - threadStep > from + threadStep) {
                to = n - threadStep;
            } else {
                to = from + threadStep;
            }
            int finalFrom = from;
            int finalTo = to;
            executorService.submit(() -> {
                try {
                    gauss(K, F, finalFrom, finalTo);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                countDownLatch.countDown();
            });
            from += threadStep;
        }

        countDownLatch.await();
        executorService.shutdown();
        return result(K, F);
    }

    static void gauss(BigDecimal[][] K, BigDecimal[] F, int from, int to) throws Exception {
        int n = F.length;

        for (int p = from; p < to; p++) {
            int max = p;
            for (int i = p + 1; i < n; i++) {
                if (K[i][p].abs().compareTo(K[max][p].abs())==1) {
                    max = i;
                }
            }
            BigDecimal[] temp = K[p];
            K[p] = K[max];
            K[max] = temp;

            BigDecimal t = F[p];
            F[p] = F[max];
            F[max] = t;

            if (K[p][p].abs().compareTo(BigDecimal.valueOf(0.0000000001))==-1) {
                throw new Exception("No solutions");
            }

            for (int i = p + 1; i < n; i++) {
                BigDecimal alpha = K[i][p].divide(K[p][p], n, RoundingMode.HALF_UP);
                F[i] = F[i].subtract(alpha.multiply(F[p]));
                for (int j = p; j < n; j++) {
                    K[i][j] = K[i][j].subtract(alpha.multiply(K[p][j]));
                }
            }
        }
    }

    private BigDecimal[] result(BigDecimal[][] K, BigDecimal[] F) {
        int n = F.length;
        BigDecimal[] x = new BigDecimal[n];
        for (int i = n - 1; i >= 0; i--) {
            BigDecimal sum = BigDecimal.valueOf(0);
            for (int j = i + 1; j < n; j++) {
                sum = sum.add(K[i][j].multiply(x[j]));
            }
            x[i] = (F[i].subtract(sum)).divide(K[i][i], n, RoundingMode.HALF_UP);
        }
        return x;
    }

}