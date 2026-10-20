package eu.mapidev.calculator.phoenix;

import eu.mapidev.calculator.phoenix.engine.ArithmeticOperation;
import eu.mapidev.calculator.phoenix.engine.CalculatorInput;
import eu.mapidev.calculator.phoenix.engine.Command;
import javafx.animation.PauseTransition;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;

import java.util.Optional;

public final class CalculatorKeyBindings {

    private CalculatorKeyBindings() {
    }

    public static Optional<CalculatorInput> findInputByEvent(KeyEvent keyEvent) {
        KeyCode code = keyEvent.getCode();

        // US layout: Shift+= is "+", Shift+8 is "*"
        if (isOnlyShiftDown(keyEvent)) {
            if (code == KeyCode.EQUALS)
                return Optional.of(ArithmeticOperation.ADD);
            if (code == KeyCode.DIGIT8)
                return Optional.of(ArithmeticOperation.MULTIPLY);
        }

        return Optional.ofNullable(switch (code) {
            case ENTER, EQUALS -> Command.EQUALS;
            case NUMBER_SIGN -> Command.CHANGE_SIGN;
            case COMMA, SEPARATOR, DECIMAL -> Command.COMMA;
            case DELETE, ESCAPE -> Command.CLEAR;
            case PLUS, ADD -> ArithmeticOperation.ADD;
            case MINUS, SUBTRACT -> ArithmeticOperation.SUBTRACT;
            case MULTIPLY, ASTERISK -> ArithmeticOperation.MULTIPLY;
            case DIVIDE, SLASH -> ArithmeticOperation.DIVIDE;
            // DIGIT0..9 are named "0".."9", NUMPAD0..9 "Numpad 0".."Numpad 9"
            default -> code.isDigitKey() ? CalculatorInput.fromToken(code.getName().substring(code.getName().length() - 1)) : null;
        });
    }

    public static void fireWithClickAnimation(Button button) {
        button.arm();
        PauseTransition pause = new PauseTransition(Duration.seconds(0.05));
        pause.setOnFinished(event -> button.disarm());
        pause.play();
        button.fire();
    }

    private static boolean isOnlyShiftDown(KeyEvent keyEvent) {
        return keyEvent.isShiftDown() && !keyEvent.isControlDown() && !keyEvent.isAltDown() && !keyEvent.isMetaDown();
    }
}
