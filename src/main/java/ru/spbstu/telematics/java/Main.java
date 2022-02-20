package ru.spbstu.telematics.java;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;


public class Main {
    public static void main(String[] args) {
        int countOfThreads = 4;
        int countOfElements = 1000;
        for (int el=10; el<=countOfElements; el+=100){
            for (int th=1; th<=countOfThreads; th++){
                FiniteMethod fem = new FiniteMethod(el, th, 0, 1, 1, 0);
                System.out.println("Количество элементов:" + el);
                System.out.println("Потоков:" + th);
                Instant start = Instant.now();
                try {
                    BigDecimal [] res = fem.solve();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                Instant finish = Instant.now();
                System.out.printf("Время работы = %d миллисекунд%n", Duration.between(start, finish).toMillis());
            }
        }
    }
}
