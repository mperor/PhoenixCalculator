package eu.mapidev.calculator.phoenix.engine;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Key-sequence scenarios: space-separated button labels, expected result and memory line.
 */
class CalculatorScenarioTest {

    @ParameterizedTest(name = "[{0}] -> result \"{1}\", memory \"{2}\"")
    @CsvSource(delimiter = '|', textBlock = """
            # keys                                    | result             | memory
            1                                         | 1                  | ''
            1 2 3 4 5 6 7 8 9                         | 123456789          | ''
            0 0 0 1 0 0 0                             | 1000               | ''
            1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8       | 1234567890123456   | ''
            0 , 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7     | 0,123456789012345  | ''

            # comma
            1 , 0 0 1                                 | 1,001              | ''
            , 1 0 0                                   | 0,100              | ''
            , 1 0 0 =                                 | 0,1                | 0,1 =
            , , 0                                     | 0,0                | ''
            , , 0 =                                   | 0                  | 0 =

            # arithmetic
            2 + 2 =                                   | 4                  | 2 + 2 =
            2 - 2 =                                   | 0                  | 2 - 2 =
            2 x 2 =                                   | 4                  | 2 x 2 =
            2 / 2 =                                   | 1                  | 2 / 2 =
            2 + 2 +                                   | 4                  | 4 +
            2 + 2 + = =                               | 12                 | 8 + 4 =
            1 + 2 = =                                 | 5                  | 3 + 2 =

            # "=" completes the calculation: a new number after it starts a new one
            2 0 0 + 1 0 = 5 x 2 =                     | 10                 | 5 x 2 =
            2 0 0 + 1 0 = 5 +                         | 5                  | 5 +
            2 0 0 + 1 0 = , 5 +                       | 0,5                | 0,5 +
            1 + 2 = 4 =                               | 4                  | 4 =
            2 + =                                     | 4                  | 2 + 2 =
            1 , 5 +                                   | 1,5                | 1,5 +
            1 , 5 + 2 =                               | 3,5                | 1,5 + 2 =

            # precision and number format
            1 0 0 0 0 0 0 0 0 / 1 =                   | 100000000          | 100000000 / 1 =
            1 0 0 0 0 0 0 0 0 / 1 +                   | 100000000          | 100000000 +
            1 2 3 4 5 6 7 8 / 1 =                     | 12345678           | 12345678 / 1 =
            1 / 3 =                                   | 0,3333333333333333 | 1 / 3 =

            # percent
            5 0 %                                     | 0,5                | ''
            2 0 0 + 1 0 %                             | 20                 | 200 +
            2 0 0 x 1 0 %                             | 0,1                | 200 x
            2 0 0 + 1 0 = %                           | 2,1                | 200 + 10 =
            2 0 0 + 1 0 = % +                         | 2,1                | 2,1 +

            # change sign
            5 +/-                                     | -5                 | ''
            5 +/- +/-                                 | 5                  | ''
            +/-                                       | 0                  | ''
            5 +/- + 3 =                               | -2                 | -5 + 3 =
            2 + 2 = +/-                               | -4                 | 2 + 2 =
            2 + 2 = +/- 3                             | 3                  | 2 + 2 =
            2 + 2 = +/- =                             | -2                 | -4 + 2 =
            2 + 2 = +/- x 3 =                         | -12                | -4 x 3 =

            # division by zero
            1 / 0 =                                   | undefined          | ''
            1 / 0 +                                   | undefined          | ''
            0 / 0 =                                   | undefined          | ''
            1 / 0 = +/- % = +                         | undefined          | ''
            1 / 0 = 5 + 2 =                           | 7                  | 5 + 2 =
            1 / 0 = , 5                               | 0,5                | ''

            # backspace
            1 2 3 ⌫                                   | 12                 | ''
            1 2 3 ⌫ ⌫ ⌫                               | 0                  | ''
            1 2 3 ⌫ ⌫ ⌫ ⌫ 7                           | 7                  | ''
            1 , 5 ⌫                                   | 1,                 | ''
            1 , 5 ⌫ ⌫ 7                               | 17                 | ''
            1 2 +/- ⌫                                 | -1                 | ''
            5 +/- ⌫                                   | 0                  | ''
            1 2 ⌫ + 3 =                               | 4                  | 1 + 3 =
            1 2 + ⌫                                   | 12                 | 12 +
            1 + 2 = ⌫                                 | 3                  | 1 + 2 =
            1 / 0 = ⌫                                 | undefined          | ''

            # clear
            C                                         | 0                  | ''
            1 + 2 = C                                 | 0                  | ''
            1 + 2 C 3 =                               | 3                  | 3 =
            """)
    void shouldShowResultAndMemoryAfterKeys(String keys, String expectedResult, String expectedMemory) {
        var state = CalculatorState.initial();
        for (String key : keys.trim().split("\\s+"))
            state = CalculatorEngine.apply(state, CalculatorInput.fromToken(key));

        var finalState = state;
        assertAll(
                () -> assertEquals(expectedResult, finalState.display(), "result"),
                () -> assertEquals(expectedMemory, finalState.memory(), "memory"));
    }

    @Test
    void shouldStartWithZeroAndEmptyMemory() {
        var state = CalculatorState.initial();

        assertEquals("0", state.display());
        assertEquals("", state.memory());
    }
}
