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
}