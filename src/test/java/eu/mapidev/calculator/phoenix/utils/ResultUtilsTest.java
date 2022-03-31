package eu.mapidev.calculator.phoenix.utils;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ResultUtilsTest {

    @Test
    void shouldSafeConvertStringToBigDecimal() {
        assertEquals(BigDecimal.ZERO, ResultUtils.safeConvertStringToBigDecimal("0,"));
        assertEquals(BigDecimal.ONE, ResultUtils.safeConvertStringToBigDecimal("1,00"));
        assertEquals(new BigDecimal("13.105"), ResultUtils.safeConvertStringToBigDecimal("13,1050"));
        assertEquals(BigDecimal.ZERO, ResultUtils.safeConvertStringToBigDecimal("NOT A NUMBER"));
    }

    @Test
    void shouldConvertBigDecimalToString(){
        assertEquals("0,123", ResultUtils.convertBigDecimalToString(new BigDecimal("0.123")));
        assertEquals("10", ResultUtils.convertBigDecimalToString(BigDecimal.TEN));
        assertEquals("0", ResultUtils.convertBigDecimalToString(BigDecimal.ZERO));
        assertEquals("12", ResultUtils.convertBigDecimalToString(new BigDecimal("12.00")));
        assertEquals("12,1", ResultUtils.convertBigDecimalToString(new BigDecimal("12.1000")));
    }
}