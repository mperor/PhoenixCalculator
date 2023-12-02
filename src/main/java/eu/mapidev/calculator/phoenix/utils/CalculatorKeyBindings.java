package eu.mapidev.calculator.phoenix.utils;

import javafx.animation.PauseTransition;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;

import static eu.mapidev.calculator.phoenix.utils.ArithmeticOperation.*;

public class CalculatorKeyBindings {

    private final static KeyCombination ADD_COMBINATION = new KeyCodeCombination(KeyCode.EQUALS, KeyCombination.SHIFT_DOWN);
    private final static KeyCombination MULTIPLY_COMBINATION = new KeyCodeCombination(KeyCode.DIGIT8, KeyCombination.SHIFT_DOWN);


    public static String findButtonTextByEvent(KeyEvent keyEvent) {
        if (ADD_COMBINATION.match(keyEvent))
            return ADD.getSign();
        else if (MULTIPLY_COMBINATION.match(keyEvent))
            return MULTIPLY.getSign();

        switch (keyEvent.getCode()) {
            case ENTER:
            case EQUALS:
                return "=";
            case NUMBER_SIGN:
                return "+/-";
            case COMMA:
            case SEPARATOR:
            case DECIMAL:
                return ",";
            case DELETE:
            case ESCAPE:
                return "C";
            case PLUS:
            case ADD:
                return ADD.getSign();
            case MINUS:
            case SUBTRACT:
                return SUBTRACT.getSign();
            case MULTIPLY:
            case ASTERISK:
                return MULTIPLY.getSign();
            case DIVIDE:
            case SLASH:
                return DIVIDE.getSign();
            case NUMPAD0:
            case NUMPAD1:
            case NUMPAD2:
            case NUMPAD3:
            case NUMPAD4:
            case NUMPAD5:
            case NUMPAD6:
            case NUMPAD7:
            case NUMPAD8:
            case NUMPAD9:
                String name = keyEvent.getCode().getName();
                return name.substring(name.length() - 1);
            case DIGIT0:
            case DIGIT1:
            case DIGIT2:
            case DIGIT3:
            case DIGIT4:
            case DIGIT5:
            case DIGIT6:
            case DIGIT7:
            case DIGIT8:
            case DIGIT9:
                return keyEvent.getCode().getName();
        }

        return null;
    }

    public static void buttonFireWithClickAnimation(Button button) {
        button.arm();
        PauseTransition pause = new PauseTransition(Duration.seconds(0.05));
        pause.setOnFinished(event -> button.disarm());
        pause.play();
        button.fire();
    }
}
