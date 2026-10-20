package eu.mapidev.calculator.phoenix.engine;

import eu.mapidev.calculator.phoenix.engine.CalculatorState.LastInput;

import java.math.BigDecimal;
import java.util.Optional;

import static eu.mapidev.calculator.phoenix.engine.CalculatorState.DEFAULT_DISPLAY;

/**
 * Calculator logic as a pure function: {@code (state, input) -> next state}.
 */
public final class CalculatorEngine {

    // Matches the 16 significant digits used by ArithmeticOperation
    public static final int MAX_INPUT_DIGITS = 16;

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private CalculatorEngine() {
    }

    public static CalculatorState apply(CalculatorState state, CalculatorInput input) {
        if (state.isError() && !isAcceptedAfterError(input))
            return state;

        return switch (input) {
            case CalculatorDigit digit -> inputDigit(state, digit);
            case ArithmeticOperation operation -> applyOperation(state, operation);
            case Command.COMMA -> addComma(state);
            case Command.CHANGE_SIGN -> changeSign(state);
            case Command.PERCENT -> applyPercent(state);
            case Command.EQUALS -> calculateResult(state);
            case Command.CLEAR -> CalculatorState.initial();
        };
    }

    // After an error only a new number or clear is accepted
    private static boolean isAcceptedAfterError(CalculatorInput input) {
        return input instanceof CalculatorDigit || input == Command.COMMA || input == Command.CLEAR;
    }

    private static CalculatorState inputDigit(CalculatorState state, CalculatorDigit digit) {
        String display = state.display();
        if (DEFAULT_DISPLAY.equals(display) || state.lastInput() != LastInput.NUMBER)
            return startNumber(state, digit.token());

        if (display.chars().filter(Character::isDigit).count() >= MAX_INPUT_DIGITS)
            return state;

        return state.withTypedNumber(display + digit.token());
    }

    private static CalculatorState addComma(CalculatorState state) {
        if (state.lastInput() != LastInput.NUMBER)
            return startNumber(state, DEFAULT_DISPLAY + DisplayFormat.COMMA);

        String display = state.display();
        if (display.contains(DisplayFormat.COMMA))
            return state;

        return state.withTypedNumber(display + DisplayFormat.COMMA);
    }

    private static CalculatorState changeSign(CalculatorState state) {
        String display = state.display();
        if (DEFAULT_DISPLAY.equals(display))
            return state;

        String negated = display.startsWith("-") ? display.substring(1) : "-" + display;
        // On a result, negate the result itself: the next digit still starts a new number and "=" continues from it
        if (state.lastInput() == LastInput.RESULT)
            return new CalculatorState(negated, state.memory(), state.accumulator().negate(),
                    state.pendingOperation(), state.repeatOperand(), LastInput.RESULT);

        return state.withTypedNumber(negated);
    }

    // With a pending + or -, x% means x percent of the left operand (200 + 10% -> 20);
    // after "=" the calculation is complete, so x% is just x / 100
    private static CalculatorState applyPercent(CalculatorState state) {
        BigDecimal percent = DisplayFormat.parse(state.display()).divide(HUNDRED);
        ArithmeticOperation operation = state.lastInput() == LastInput.RESULT ? null : state.pendingOperation();
        BigDecimal result = operation == ArithmeticOperation.ADD || operation == ArithmeticOperation.SUBTRACT
                ? state.accumulator().multiply(percent)
                : percent;

        return startNumber(state, DisplayFormat.format(result));
    }

    // "=" completes the calculation: a number entered after it starts a new one, so the operation
    // kept for repeating "=" is dropped. The memory line keeps showing the last expression.
    private static CalculatorState startNumber(CalculatorState state, String display) {
        if (state.lastInput() == LastInput.RESULT)
            return new CalculatorState(display, state.memory(), BigDecimal.ZERO, null, BigDecimal.ZERO, LastInput.NUMBER);

        return state.withTypedNumber(display);
    }

    private static CalculatorState applyOperation(CalculatorState state, ArithmeticOperation operation) {
        BigDecimal value = DisplayFormat.parse(state.display());
        Optional<BigDecimal> result = state.pendingOperation() != null && state.lastInput() == LastInput.NUMBER
                ? calculate(state.pendingOperation(), state.accumulator(), value)
                : Optional.of(value);

        return result.map(accumulator -> {
            String display = DisplayFormat.format(accumulator);
            return new CalculatorState(display, display + " " + operation.getSign(),
                    accumulator, operation, state.repeatOperand(), LastInput.OPERATION);
        }).orElseGet(CalculatorState::error);
    }

    private static CalculatorState calculateResult(CalculatorState state) {
        BigDecimal value = DisplayFormat.parse(state.display());
        BigDecimal repeatOperand = state.lastInput() == LastInput.RESULT ? state.repeatOperand() : value;
        ArithmeticOperation operation = state.pendingOperation();

        if (operation == null) {
            String display = DisplayFormat.format(value);
            return new CalculatorState(display, display + " =", value, null, repeatOperand, LastInput.RESULT);
        }

        return calculate(operation, state.accumulator(), repeatOperand).map(result -> {
            String memory = String.format("%s %s %s =", DisplayFormat.format(state.accumulator()),
                    operation.getSign(), DisplayFormat.format(repeatOperand));
            return new CalculatorState(DisplayFormat.format(result), memory,
                    result, operation, repeatOperand, LastInput.RESULT);
        }).orElseGet(CalculatorState::error);
    }

    // Empty when the operation is undefined, e.g. division by zero
    private static Optional<BigDecimal> calculate(ArithmeticOperation operation, BigDecimal left, BigDecimal right) {
        try {
            return Optional.of(operation.getOperation().apply(left, right));
        } catch (ArithmeticException e) {
            return Optional.empty();
        }
    }
}
