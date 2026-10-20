package eu.mapidev.calculator.phoenix.engine;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Anything the user can press: a digit, an arithmetic operation or a command.
 */
public sealed interface CalculatorInput permits CalculatorDigit, ArithmeticOperation, Command {

    /**
     * Short text identifying the input, e.g. "7", "+" or "+/-".
     */
    String token();

    static CalculatorInput fromToken(String token) {
        return Stream.<CalculatorInput[]>of(CalculatorDigit.values(), ArithmeticOperation.values(), Command.values())
                .flatMap(Arrays::stream)
                .filter(input -> input.token().equals(token))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown calculator input: " + token));
    }
}
