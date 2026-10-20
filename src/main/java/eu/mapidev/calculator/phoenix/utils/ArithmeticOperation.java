package eu.mapidev.calculator.phoenix.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.BinaryOperator;

public enum ArithmeticOperation {

    // Results are rounded to 16 significant digits (DECIMAL64): bounded, and exact for numbers that can be typed in
    ADD("+", (decimal, augend) -> decimal.add(augend, MathContext.DECIMAL64)),
    SUBTRACT("-", (decimal, subtrahend) -> decimal.subtract(subtrahend, MathContext.DECIMAL64)),
    MULTIPLY("x", (decimal, multiplicand) -> decimal.multiply(multiplicand, MathContext.DECIMAL64)),
    DIVIDE("/", (decimal, divisor) -> decimal.divide(divisor, MathContext.DECIMAL64));

    private final String sign;
    private final BinaryOperator<BigDecimal> operation;

    ArithmeticOperation(String sign, BinaryOperator<BigDecimal> operation) {
        this.sign = sign;
        this.operation = operation;
    }

    public String getSign() {
        return sign;
    }

    public BinaryOperator<BigDecimal> getOperation() {
        return operation;
    }

    public static Optional<ArithmeticOperation> findBySign(final String sign) {
        return Arrays.stream(ArithmeticOperation.values())
                .filter(value -> value.sign.equals(sign))
                .findFirst();
    }

}
