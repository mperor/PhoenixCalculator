package eu.mapidev.calculator.phoenix.utils;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ArithmeticOperationTest {

    @Test
    void shouldAllowToGetFunctionByArithmeticOperationAndCalculateResult() {
        assertEquals(BigDecimal.valueOf(2), ArithmeticOperation.ADD.getOperation().apply(BigDecimal.ONE, BigDecimal.ONE));
        assertEquals(BigDecimal.ZERO, ArithmeticOperation.SUBTRACT.getOperation().apply(BigDecimal.ONE, BigDecimal.ONE));
        assertEquals(BigDecimal.TEN, ArithmeticOperation.MULTIPLY.getOperation().apply(BigDecimal.ONE, BigDecimal.TEN));
        assertEquals(BigDecimal.valueOf(0.1), ArithmeticOperation.DIVIDE.getOperation().apply(BigDecimal.ONE, BigDecimal.TEN));
    }

    @Test
    void shouldAllowArithmeticOperationToFindBySign() {
        assertEquals(ArithmeticOperation.ADD, ArithmeticOperation.findBySign("+").get());
        assertEquals(ArithmeticOperation.SUBTRACT, ArithmeticOperation.findBySign("-").get());
        assertEquals(ArithmeticOperation.MULTIPLY, ArithmeticOperation.findBySign("x").get());
        assertEquals(ArithmeticOperation.DIVIDE, ArithmeticOperation.findBySign("/").get());
        assertThrows(NoSuchElementException.class, () -> ArithmeticOperation.findBySign(".").get());
    }
}