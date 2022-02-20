package ru.spbstu.telematics.java;

import junit.framework.TestSuite;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.MathContext;


public class AppTest
        extends TestSuite
{
    private BigDecimal [] expectedRes = {BigDecimal.valueOf(0.0804),
            BigDecimal.valueOf(0.2159),
            BigDecimal.valueOf(0.2086),
            BigDecimal.valueOf(0.2093),
            BigDecimal.valueOf(0.2093),
            BigDecimal.valueOf(0.2093)};
    @Test
    public void femTest(){
        FiniteMethod fem = new FiniteMethod(5, 1, 0, 1, 1, 0);
        try {
            BigDecimal[] res = fem.solve();
            MathContext mc = new MathContext(3);
            for (int i=0; i<6; i++)
                Assert.assertEquals(expectedRes[i].round(mc), res[i].round(mc));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Test
    public void femTestPar(){
        FiniteMethod fem = new FiniteMethod(5, 3, 0, 1, 1, 0);
        try {
            BigDecimal[] res = fem.solve();
            MathContext mc = new MathContext(3);
            for (int i=0; i<6; i++)
                Assert.assertEquals(expectedRes[i].round(mc), res[i].round(mc));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}