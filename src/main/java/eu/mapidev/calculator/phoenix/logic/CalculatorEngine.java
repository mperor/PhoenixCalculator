package eu.mapidev.calculator.phoenix.logic;

import eu.mapidev.calculator.phoenix.utils.ArithmeticOperation;
import eu.mapidev.calculator.phoenix.utils.ResultUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class CalculatorEngine {

    public static final String DEFAULT_RESULT = "0";
    private Label lblMemory = new Label();
    private Label lblResult = new Label();

    private Optional<ArithmeticOperation> storedOperation = Optional.empty();
    private BigDecimal storedResult = BigDecimal.ZERO;
    private CalledCalculatorOption calledCalculatorOption = CalledCalculatorOption.RESET;
    private BigDecimal storedFactor = BigDecimal.ZERO;

    public void setOnResultChanged(Consumer<String> resultConsumer) {
        lblResult.addListener(resultConsumer);
    }

    public void setOnMemoryChanged(Consumer<String> memoryConsumer) {
        lblMemory.addListener(memoryConsumer);
    }

    public void reset() {
        lblMemory.setText(DEFAULT_RESULT);
        lblResult.setText(DEFAULT_RESULT);

        storedOperation = Optional.empty();
        storedResult = BigDecimal.ZERO;

        calledCalculatorOption = CalledCalculatorOption.RESET;
    }

    public void concatNumber(String textNumber) {
        String result = lblResult.getText();
        if (DEFAULT_RESULT.equals(result) || calledCalculatorOption != CalledCalculatorOption.INPUT_NUMBER)
            lblResult.setText(textNumber);
        else
            lblResult.setText(result + textNumber);

        calledCalculatorOption = CalledCalculatorOption.INPUT_NUMBER;
    }

    public void doPercent() {
        String result = lblResult.getText();
        BigDecimal decimalResult = ResultUtils.safeConvertStringToBigDecimal(result);

        storedOperation.filter(operation -> EnumSet.of(ArithmeticOperation.ADD, ArithmeticOperation.SUBTRACT).contains(operation))
                .ifPresentOrElse(arithmeticOperation -> lblResult.setText(ResultUtils.convertBigDecimalToString(storedResult.multiply(decimalResult.divide(BigDecimal.valueOf(100))))),
                        () -> lblResult.setText(ResultUtils.convertBigDecimalToString(decimalResult.divide(BigDecimal.valueOf(100)))));

        calledCalculatorOption = CalledCalculatorOption.INPUT_NUMBER;
    }

    public void changeSign() {
        String result = lblResult.getText();
        if (DEFAULT_RESULT.equals(result))
            return;

        lblResult.setText(result.startsWith("-") ? result.substring(1) : "-" + result);

        calledCalculatorOption = CalledCalculatorOption.INPUT_NUMBER;
    }

    public void addComma() {
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

    public void doOperation(String operationSign) {
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

    public void calculateResult() {
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

    private static class Label {

        private List<Consumer<String>> consumers = new ArrayList<>();

        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
            consumers.forEach(c -> c.accept(text));
        }

        public void addListener(Consumer<String> consumer) {
            consumers.add(consumer);
        }
    }

}
