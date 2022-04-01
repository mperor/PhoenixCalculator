package eu.mapidev.calculator.phoenix;

import eu.mapidev.calculator.phoenix.utils.ArithmeticOperation;
import eu.mapidev.calculator.phoenix.utils.ResultUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

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