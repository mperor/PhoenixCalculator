package eu.mapidev.calculator.phoenix.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.BinaryOperator;

public enum ArithmeticOperation {

    ADD("+", BigDecimal::add),
    SUBTRACT("-", BigDecimal::subtract),
    MULTIPLY("x", BigDecimal::multiply),
    DIVIDE("/", ArithmeticOperation::divideWithTerminating);

    private String sign;
    private BinaryOperator<BigDecimal> operation;

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

    private static BigDecimal divideWithTerminating(BigDecimal decimal, BigDecimal divisor) {
        return decimal.divide(divisor, MathContext.DECIMAL32);
    }

}
