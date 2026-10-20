package eu.mapidev.calculator.phoenix.engine;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculatorInputTest {

    @ParameterizedTest
    @CsvSource(delimiter = '|', textBlock = """
            0   | _0
            9   | _9
            +   | ADD
            -   | SUBTRACT
            x   | MULTIPLY
            /   | DIVIDE
            ,   | COMMA
            +/- | CHANGE_SIGN
            %   | PERCENT
            =   | EQUALS
            C   | CLEAR
            """)
    void shouldFindInputByToken(String token, String expectedName) {
        assertEquals(expectedName, ((Enum<?>) CalculatorInput.fromToken(token)).name());
    }

    @Test
    void shouldFindEveryInputByItsOwnToken() {
        Stream.<CalculatorInput[]>of(CalculatorDigit.values(), ArithmeticOperation.values(), Command.values())
                .flatMap(Arrays::stream)
                .forEach(input -> assertEquals(input, CalculatorInput.fromToken(input.token())));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", ".", "10", "*", "c"})
    void shouldRejectUnknownToken(String token) {
        assertThrows(IllegalArgumentException.class, () -> CalculatorInput.fromToken(token));
    }
}
