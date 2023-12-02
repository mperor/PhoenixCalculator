package eu.mapidev.calculator.phoenix.logic;

import eu.mapidev.calculator.phoenix.utils.ArithmeticOperation;
import eu.mapidev.calculator.phoenix.utils.CalculatorDigit;
import eu.mapidev.calculator.phoenix.utils.ResultUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Consumer;

public class BasicCalculatorEngine {
    public static final String DEFAULT_RESULT = "0";

    protected Label lblResult = new Label(DEFAULT_RESULT);
    protected Optional<ArithmeticOperation> storedOperation = Optional.empty();
    protected BigDecimal storedResult = BigDecimal.ZERO;
    protected CalledCalculatorOption calledCalculatorOption = CalledCalculatorOption.RESET;
    protected BigDecimal storedFactor = BigDecimal.ZERO;

    public void setOnResultChanged(Consumer<String> resultConsumer) {
        lblResult.setTextChangedConsumer(resultConsumer);
    }

    public BasicCalculatorEngine inputDigit(CalculatorDigit digit) {
        String result = lblResult.getText();
        if (DEFAULT_RESULT.equals(result) || calledCalculatorOption != CalledCalculatorOption.INPUT_NUMBER)
            lblResult.setText(digit.toString());
        else
            lblResult.setText(result + digit.toString());

        calledCalculatorOption = CalledCalculatorOption.INPUT_NUMBER;
        return this;
    }

    public BasicCalculatorEngine inputDigits(CalculatorDigit... digits) {
        Arrays.stream(digits).forEach(this::inputDigit);
        return this;
    }

    public BasicCalculatorEngine changeSign() {
        String result = lblResult.getText();
        if (DEFAULT_RESULT.equals(result))
            return this;

        lblResult.setText(result.startsWith("-") ? result.substring(1) : "-" + result);

        calledCalculatorOption = CalledCalculatorOption.INPUT_NUMBER;
        return this;
    }

    public BasicCalculatorEngine reset() {
        lblResult.setText(DEFAULT_RESULT);
        storedOperation = Optional.empty();
        storedResult = BigDecimal.ZERO;
        storedFactor = BigDecimal.ZERO;

        calledCalculatorOption = CalledCalculatorOption.RESET;
        return this;
    }

    public BasicCalculatorEngine addComma() {
        if (calledCalculatorOption != CalledCalculatorOption.INPUT_NUMBER) {
            lblResult.setText(DEFAULT_RESULT + ResultUtils.COMMA);
            calledCalculatorOption = CalledCalculatorOption.INPUT_NUMBER;
            return this;
        }

        String result = lblResult.getText();
        if (result.contains(ResultUtils.COMMA))
            return this;

        lblResult.setText(result + ResultUtils.COMMA);
        calledCalculatorOption = CalledCalculatorOption.INPUT_NUMBER;
        return this;
    }

    public BasicCalculatorEngine doPercent() {
        String result = lblResult.getText();
        BigDecimal decimalResult = ResultUtils.safeConvertStringToBigDecimal(result);

        storedOperation.filter(operation -> EnumSet.of(ArithmeticOperation.ADD, ArithmeticOperation.SUBTRACT).contains(operation))
                .ifPresentOrElse(arithmeticOperation -> lblResult.setText(ResultUtils.convertBigDecimalToString(storedResult.multiply(decimalResult.divide(BigDecimal.valueOf(100))))),
                        () -> lblResult.setText(ResultUtils.convertBigDecimalToString(decimalResult.divide(BigDecimal.valueOf(100)))));

        calledCalculatorOption = CalledCalculatorOption.INPUT_NUMBER;
        return this;
    }

    public BasicCalculatorEngine add() {
        return doOperation(ArithmeticOperation.ADD);
    }


    public BasicCalculatorEngine subtract() {
        return doOperation(ArithmeticOperation.SUBTRACT);
    }


    public BasicCalculatorEngine divide() {
        return doOperation(ArithmeticOperation.DIVIDE);
    }


    public BasicCalculatorEngine multiply() {
        return doOperation(ArithmeticOperation.MULTIPLY);
    }

    public BasicCalculatorEngine doOperation(ArithmeticOperation operation) {
        BigDecimal decimal = ResultUtils.safeConvertStringToBigDecimal(lblResult.getText());
        var result = storedOperation.filter(stored -> calledCalculatorOption == CalledCalculatorOption.INPUT_NUMBER)
                .map(ArithmeticOperation::getOperation)
                .map(bigDecimalBinaryOperator -> bigDecimalBinaryOperator.apply(storedResult, decimal))
                .orElse(decimal);

        lblResult.setText(ResultUtils.convertBigDecimalToString(result));
        storedOperation = Optional.of(operation);
        storedResult = result;

        calledCalculatorOption = CalledCalculatorOption.SET_OPERATION;
        return this;
    }

    public BasicCalculatorEngine calculateResult() {
        var currentResult = ResultUtils.safeConvertStringToBigDecimal(lblResult.getText());
        if (calledCalculatorOption != CalledCalculatorOption.CALCULATE_RESULT) {
            storedFactor = currentResult;
        }

        var result = storedOperation.map(ArithmeticOperation::getOperation)
                .map(bigDecimalBinaryOperator -> bigDecimalBinaryOperator.apply(storedResult, storedFactor))
                .orElse(currentResult);

        lblResult.setText(ResultUtils.convertBigDecimalToString(result));
        storedResult = result;

        calledCalculatorOption = CalledCalculatorOption.CALCULATE_RESULT;
        return this;
    }

    protected enum CalledCalculatorOption {
        INPUT_NUMBER,
        SET_OPERATION,
        CALCULATE_RESULT,
        RESET
    }

    protected static class Label {

        private Consumer<String> textChangedConsumer = text -> {
        };

        private String text;

        public Label(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
            textChangedConsumer.accept(text);
        }

        public void setTextChangedConsumer(Consumer<String> textChangedConsumer) {
            this.textChangedConsumer = textChangedConsumer;
            textChangedConsumer.accept(text);
        }

        @Override
        public String toString() {
            return getText();
        }
    }
}
