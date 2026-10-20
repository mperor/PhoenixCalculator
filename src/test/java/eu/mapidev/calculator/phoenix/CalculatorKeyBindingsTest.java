package eu.mapidev.calculator.phoenix;

import eu.mapidev.calculator.phoenix.engine.CalculatorInput;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculatorKeyBindingsTest {

    @ParameterizedTest(name = "{0} (shift: {1}, ctrl: {2}) -> {3}")
    @CsvSource(delimiter = '|', textBlock = """
            # key code  | shift | ctrl  | expected input token, empty when ignored
            DIGIT0      | false | false | 0
            DIGIT7      | false | false | 7
            NUMPAD0     | false | false | 0
            NUMPAD9     | false | false | 9
            ENTER       | false | false | =
            EQUALS      | false | false | =
            EQUALS      | true  | false | +
            EQUALS      | true  | true  | =
            DIGIT8      | true  | false | x
            PLUS        | false | false | +
            ADD         | false | false | +
            MINUS       | false | false | -
            SUBTRACT    | false | false | -
            MULTIPLY    | false | false | x
            ASTERISK    | false | false | x
            DIVIDE      | false | false | /
            SLASH       | false | false | /
            COMMA       | false | false | ,
            DECIMAL     | false | false | ,
            SEPARATOR   | false | false | ,
            NUMBER_SIGN | false | false | +/-
            DELETE      | false | false | C
            ESCAPE      | false | false | C
            A           | false | false |
            SPACE       | false | false |
            """)
    void shouldMapKeyToCalculatorInput(KeyCode code, boolean shift, boolean ctrl, String expectedToken) {
        var keyEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", code, shift, ctrl, false, false);

        assertEquals(Optional.ofNullable(expectedToken).map(CalculatorInput::fromToken),
                CalculatorKeyBindings.findInputByEvent(keyEvent));
    }
}
