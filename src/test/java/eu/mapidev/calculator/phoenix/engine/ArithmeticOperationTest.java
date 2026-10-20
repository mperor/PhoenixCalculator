package eu.mapidev.calculator.phoenix.engine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

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
    void shouldCalculateWithSixteenSignificantDigits() {
        assertEquals(0, new BigDecimal("12345678").compareTo(
                ArithmeticOperation.DIVIDE.getOperation().apply(new BigDecimal("12345678"), BigDecimal.ONE)));
        assertEquals(new BigDecimal("0.3333333333333333"),
                ArithmeticOperation.DIVIDE.getOperation().apply(BigDecimal.ONE, new BigDecimal("3")));
        assertEquals(new BigDecimal("9.999999999999998E+31"),
                ArithmeticOperation.MULTIPLY.getOperation().apply(new BigDecimal("9999999999999999"), new BigDecimal("9999999999999999")));
    }

    @Test
    void shouldAllowArithmeticOperationToFindBySign() {
        assertEquals(ArithmeticOperation.ADD, ArithmeticOperation.findBySign("+").get());
        assertEquals(ArithmeticOperation.SUBTRACT, ArithmeticOperation.findBySign("-").get());
        assertEquals(ArithmeticOperation.MULTIPLY, ArithmeticOperation.findBySign("x").get());
        assertEquals(ArithmeticOperation.DIVIDE, ArithmeticOperation.findBySign("/").get());
        assertEquals(Optional.empty(), ArithmeticOperation.findBySign("."));
        assertThrows(NoSuchElementException.class, () -> ArithmeticOperation.findBySign(".").get());
    }

}