package ru.spbstu.telematics.java;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FiniteMethod {

    private int n;// количество узлов
    private int m;// количество элементов
    private int nThreads;// количество потоков

    private BigDecimal h, min, max; //шаг сетки, координаты отрезка
    private BigDecimal [] x; //координаты элементов
    private BigDecimal [][] K; // матрица жесткости
    private BigDecimal [] F;// вектор правых частей
    private BigDecimal [] bounderCondition; // граничные условия

    private final ExecutorService executorService;

    public FiniteMethod(int countOfElements, int countOfThreads, double a, double b, double fA, double fB){
        m = countOfElements;
        n = m + 1;
        h = new BigDecimal(b - a).divide(BigDecimal.valueOf(m), n, RoundingMode.HALF_UP);
        min = BigDecimal.valueOf(a);
        max = BigDecimal.valueOf(b);
        bounderCondition = new BigDecimal[2];
        bounderCondition[0] = BigDecimal.valueOf(fA);
        bounderCondition[1] = BigDecimal.valueOf(fB);

        nThreads = countOfThreads;
        executorService = Executors.newFixedThreadPool(nThreads);
    }



    public BigDecimal [] solve() throws Exception {
        initCoordinates();
        createK();
        createF();
        final LinearSolver solver = new LinearSolver(nThreads);
        BigDecimal[] res =  solver.solveParallel(K, F, nThreads);
        return res;
    }


    private void initCoordinates(){
        x = new BigDecimal[n];
        for (int i=0; i<n; i++){
            x[i] = min.add(h.multiply(BigDecimal.valueOf((double)(i-1))));
        }
    }

    private void createK() throws InterruptedException {
        this.K=new BigDecimal[n][n];
        for (int i = 0; i<n; i++)
            for (int j=0; j<n; j++)
                K[i][j] = BigDecimal.valueOf(0);

        CountDownLatch countDownLatch = new CountDownLatch(n-1);


        for (int i=0; i<n-1; i++){
            int finalI = i;
            executorService.submit(() -> {
                addElementaryMatrixK(finalI);
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
    }

    private void addElementaryMatrixK(int i){
        BigDecimal [][] k = new BigDecimal[2][2];
        k[0][0]=k[1][1] = BigDecimal.valueOf(1).divide(h, n, RoundingMode.HALF_UP).
                subtract(h.subtract(BigDecimal.valueOf(3)));
        k[0][1]=k[1][0] = BigDecimal.valueOf(0).
                subtract(BigDecimal.valueOf(1).divide(h, n, RoundingMode.HALF_UP).
                        add(h.subtract(BigDecimal.valueOf(6))));
        for (int j=0; j<2;j++)
            for (int w=0; w<2; w++){
                K[i +j][i +w]=K[i +j][i +w].add(k[j][w]);
            }
    }

    private void createF() throws InterruptedException {
        this.F=new BigDecimal[n];
        for (int i=0; i<n; i++)
            F[i]=BigDecimal.valueOf(0);
        F[0]=BigDecimal.valueOf(0).subtract(bounderCondition[0]);
        F[1]=bounderCondition[1];
        ExecutorService executorService1 = Executors.newFixedThreadPool(nThreads);
        CountDownLatch countDownLatch = new CountDownLatch(n-1);
        for (int i=0; i<n-1; i++){
            int finalI = i;
            executorService1.submit(() -> {
                addElementaryMatrixF(finalI);
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService1.shutdown();
    }

    private void addElementaryMatrixF(int i){
        BigDecimal [] f = new BigDecimal[2];
        f[0] = BigDecimal.valueOf(0).
                subtract(h.subtract(BigDecimal.valueOf(2)));
        f[1] = BigDecimal.valueOf(0).
                subtract(h.subtract(BigDecimal.valueOf(2)));
        for (int j=0; j<2;j++)
            F[i+j]=F[i+j].add(f[j]);
    }

    public BigDecimal getH(){ return h;}

}