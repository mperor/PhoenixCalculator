package eu.mapidev.calculator.phoenix.engine;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Immutable state of the calculator; {@link CalculatorEngine#apply} derives the next state from it.
 *
 * @param display          text of the result line
 * @param memory           text of the expression line above the result
 * @param accumulator      left operand of the pending operation
 * @param pendingOperation operation applied by the next operation or "="; {@code null} when there is none
 * @param repeatOperand    right operand reused when "=" is pressed again
 * @param lastInput        kind of the last accepted input; decides e.g. whether a digit starts a new number
 */
public record CalculatorState(String display,
                              String memory,
                              BigDecimal accumulator,
                              ArithmeticOperation pendingOperation,
                              BigDecimal repeatOperand,
                              LastInput lastInput) {

    public static final String DEFAULT_DISPLAY = "0";
    public static final String ERROR_DISPLAY = "undefined";

    public enum LastInput {
        NUMBER,
        OPERATION,
        RESULT,
        CLEAR,
        ERROR
    }

    public CalculatorState {
        Objects.requireNonNull(display);
        Objects.requireNonNull(memory);
        Objects.requireNonNull(accumulator);
        Objects.requireNonNull(repeatOperand);
        Objects.requireNonNull(lastInput);
    }

    public static CalculatorState initial() {
        return new CalculatorState(DEFAULT_DISPLAY, "", BigDecimal.ZERO, null, BigDecimal.ZERO, LastInput.CLEAR);
    }

    static CalculatorState error() {
        return new CalculatorState(ERROR_DISPLAY, "", BigDecimal.ZERO, null, BigDecimal.ZERO, LastInput.ERROR);
    }

    public boolean isError() {
        return lastInput == LastInput.ERROR;
    }

    CalculatorState withTypedNumber(String display) {
        return new CalculatorState(display, memory, accumulator, pendingOperation, repeatOperand, LastInput.NUMBER);
    }
}
