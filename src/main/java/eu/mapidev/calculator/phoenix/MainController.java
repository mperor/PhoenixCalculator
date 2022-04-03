package eu.mapidev.calculator.phoenix;

import eu.mapidev.calculator.phoenix.utils.ArithmeticOperation;
import eu.mapidev.calculator.phoenix.utils.ResultUtils;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.math.BigDecimal;
import java.net.URL;
import java.util.EnumSet;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    public static final String DEFAULT_RESULT = "0";

    @FXML
    private GridPane root;
    @FXML
    private Label lblResult;
    @FXML
    private Label lblMemory;

    private Stage primaryStage;

    private Optional<ArithmeticOperation> storedOperation = Optional.empty();
    private BigDecimal storedResult = BigDecimal.ZERO;
    private CalledCalculatorOption calledCalculatorOption = CalledCalculatorOption.RESET;
    private BigDecimal storedFactor = BigDecimal.ZERO;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        root.setOnMousePressed(pressEvent -> {
            root.setOnMouseDragged(dragEvent -> {
                if (primaryStage == null)
                    return;

                primaryStage.setX(dragEvent.getScreenX() - pressEvent.getSceneX());
                primaryStage.setY(dragEvent.getScreenY() - pressEvent.getSceneY());
            });
        });

        root.setOnKeyPressed(keyEvent -> {
            root.getChildren().stream()
                    .filter(node -> node instanceof Button)
                    .map(node -> (Button) node)
                    .filter(button -> button.getText().equals(getBindingButtonTextByEvent(keyEvent)))
                    .findFirst()
                    .ifPresent(this::buttonFireWithClickAnimation);
        });
    }

    private String getBindingButtonTextByEvent(KeyEvent keyEvent) {
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
                return "C";
            case PLUS:
            case ADD:
                return "+";
            case MINUS:
            case SUBTRACT:
                return "-";
            case MULTIPLY:
                return "x";
            case DIVIDE:
                return "/";
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

    private void buttonFireWithClickAnimation(Button button) {
        button.arm();
        PauseTransition pause = new PauseTransition(Duration.seconds(0.1));
        pause.setOnFinished(event -> button.disarm());
        pause.play();
        button.fire();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    public void concatNumber(ActionEvent actionEvent) {
        String buttonTextNumber = ((Button) actionEvent.getSource()).getText();
        String result = lblResult.getText();
        if (DEFAULT_RESULT.equals(result) || calledCalculatorOption != CalledCalculatorOption.INPUT_NUMBER)
            lblResult.setText(buttonTextNumber);
        else
            lblResult.setText(result + buttonTextNumber);

        calledCalculatorOption = CalledCalculatorOption.INPUT_NUMBER;
    }

    @FXML
    public void doPercent(ActionEvent actionEvent) {
        String result = lblResult.getText();
        BigDecimal decimalResult = ResultUtils.safeConvertStringToBigDecimal(result);

        storedOperation.filter(operation -> EnumSet.of(ArithmeticOperation.ADD, ArithmeticOperation.SUBTRACT).contains(operation))
                .ifPresentOrElse(arithmeticOperation -> lblResult.setText(ResultUtils.convertBigDecimalToString(storedResult.multiply(decimalResult.divide(BigDecimal.valueOf(100))))),
                        () -> lblResult.setText(ResultUtils.convertBigDecimalToString(decimalResult.divide(BigDecimal.valueOf(100)))));

        calledCalculatorOption = CalledCalculatorOption.INPUT_NUMBER;
    }

    @FXML
    public void clean(ActionEvent actionEvent) {
        lblMemory.setText(DEFAULT_RESULT);
        lblResult.setText(DEFAULT_RESULT);
        storedOperation = Optional.empty();
        storedResult = BigDecimal.ZERO;

        calledCalculatorOption = CalledCalculatorOption.RESET;
    }

    @FXML
    public void changeSign(ActionEvent actionEvent) {
        String result = lblResult.getText();
        if (DEFAULT_RESULT.equals(result))
            return;

        lblResult.setText(result.startsWith("-") ? result.substring(1) : "-" + result);

        calledCalculatorOption = CalledCalculatorOption.INPUT_NUMBER;
    }

    @FXML
    public void addComma(ActionEvent actionEvent) {
        if (calledCalculatorOption != CalledCalculatorOption.INPUT_NUMBER) {
            lblResult.setText(DEFAULT_RESULT + ResultUtils.COMMA);
            calledCalculatorOption = CalledCalculatorOption.INPUT_NUMBER;
            return;
        }

        String result = lblResult.getText();
        if (result.contains(ResultUtils.COMMA))
            return;

        lblResult.setText(result + ResultUtils.COMMA);
        calledCalculatorOption = CalledCalculatorOption.INPUT_NUMBER;
    }

    @FXML
    public void doOperation(ActionEvent actionEvent) {
        String operationSign = ((Button) actionEvent.getSource()).getText();

        BigDecimal decimal = ResultUtils.safeConvertStringToBigDecimal(lblResult.getText());
        var result = storedOperation.filter(operation -> calledCalculatorOption == CalledCalculatorOption.INPUT_NUMBER)
                .map(ArithmeticOperation::getOperation)
                .map(bigDecimalBinaryOperator -> bigDecimalBinaryOperator.apply(storedResult, decimal))
                .orElse(decimal);

        lblMemory.setText(String.format("%s %s", result, operationSign));
        lblResult.setText(ResultUtils.convertBigDecimalToString(result));
        storedOperation = ArithmeticOperation.findBySign(operationSign);
        storedResult = result;

        calledCalculatorOption = CalledCalculatorOption.SET_OPERATION;
    }

    @FXML
    public void calculateResult(ActionEvent actionEvent) {
        if (storedOperation.isEmpty()) {
            lblMemory.setText(String.format("%s = ", lblResult.getText()));
            return;
        }

        var currentResult = ResultUtils.safeConvertStringToBigDecimal(lblResult.getText());
        if (calledCalculatorOption != CalledCalculatorOption.CALCULATE_RESULT) {
            storedFactor = currentResult;
        }

        var result = storedOperation.map(ArithmeticOperation::getOperation)
                .map(bigDecimalBinaryOperator -> bigDecimalBinaryOperator.apply(storedResult, storedFactor))
                .get();

        lblMemory.setText(String.format("%s %s %s =", storedResult, storedOperation.get().getSign(), storedFactor));
        lblResult.setText(ResultUtils.convertBigDecimalToString(result));
        storedResult = result;

        calledCalculatorOption = CalledCalculatorOption.CALCULATE_RESULT;
    }

    private enum CalledCalculatorOption {
        INPUT_NUMBER,
        SET_OPERATION,
        CALCULATE_RESULT,
        RESET
    }

}