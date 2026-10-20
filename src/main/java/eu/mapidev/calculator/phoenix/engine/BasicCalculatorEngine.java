package eu.mapidev.calculator.phoenix.engine;

import eu.mapidev.calculator.phoenix.engine.ArithmeticOperation;
import eu.mapidev.calculator.phoenix.engine.CalculatorDigit;
import eu.mapidev.calculator.phoenix.engine.DisplayFormat;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Consumer;

public class BasicCalculatorEngine {
    public static final String DEFAULT_RESULT = "0";
    public static final String ERROR_RESULT = "undefined";
    // Matches the 16 significant digits used by ArithmeticOperation
    public static final int MAX_INPUT_DIGITS = 16;

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
        else if (result.chars().filter(Character::isDigit).count() < MAX_INPUT_DIGITS)
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
        if (DEFAULT_RESULT.equals(result) || isError())
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
            lblResult.setText(DEFAULT_RESULT + DisplayFormat.COMMA);
            calledCalculatorOption = CalledCalculatorOption.INPUT_NUMBER;
            return this;
        }

        String result = lblResult.getText();
        if (result.contains(DisplayFormat.COMMA))
            return this;

        lblResult.setText(result + DisplayFormat.COMMA);
        calledCalculatorOption = CalledCalculatorOption.INPUT_NUMBER;
        return this;
    }

    public BasicCalculatorEngine doPercent() {
        if (isError())
            return this;

        String result = lblResult.getText();
        BigDecimal decimalResult = DisplayFormat.parse(result);

        storedOperation.filter(operation -> EnumSet.of(ArithmeticOperation.ADD, ArithmeticOperation.SUBTRACT).contains(operation))
                .ifPresentOrElse(arithmeticOperation -> lblResult.setText(DisplayFormat.format(storedResult.multiply(decimalResult.divide(BigDecimal.valueOf(100))))),
                        () -> lblResult.setText(DisplayFormat.format(decimalResult.divide(BigDecimal.valueOf(100)))));

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
        if (isError())
            return this;

        BigDecimal decimal = DisplayFormat.parse(lblResult.getText());
        BigDecimal result;
        try {
            result = storedOperation.filter(stored -> calledCalculatorOption == CalledCalculatorOption.INPUT_NUMBER)
                    .map(ArithmeticOperation::getOperation)
                    .map(bigDecimalBinaryOperator -> bigDecimalBinaryOperator.apply(storedResult, decimal))
                    .orElse(decimal);
        } catch (ArithmeticException e) {
            return showError();
        }

        lblResult.setText(DisplayFormat.format(result));
        storedOperation = Optional.of(operation);
        storedResult = result;

        calledCalculatorOption = CalledCalculatorOption.SET_OPERATION;
        return this;
    }

    public BasicCalculatorEngine calculateResult() {
        if (isError())
            return this;

        var currentResult = DisplayFormat.parse(lblResult.getText());
        if (calledCalculatorOption != CalledCalculatorOption.CALCULATE_RESULT) {
            storedFactor = currentResult;
        }

        BigDecimal result;
        try {
            result = storedOperation.map(ArithmeticOperation::getOperation)
                    .map(bigDecimalBinaryOperator -> bigDecimalBinaryOperator.apply(storedResult, storedFactor))
                    .orElse(currentResult);
        } catch (ArithmeticException e) {
            return showError();
        }

        lblResult.setText(DisplayFormat.format(result));
        storedResult = result;

        calledCalculatorOption = CalledCalculatorOption.CALCULATE_RESULT;
        return this;
    }

    protected boolean isError() {
        return calledCalculatorOption == CalledCalculatorOption.ERROR;
    }

    // Clears the calculation and shows ERROR_RESULT; only digits, comma and reset are accepted until a new number starts
    private BasicCalculatorEngine showError() {
        reset();
        lblResult.setText(ERROR_RESULT);
        calledCalculatorOption = CalledCalculatorOption.ERROR;
        return this;
    }

    protected enum CalledCalculatorOption {
        INPUT_NUMBER,
        SET_OPERATION,
        CALCULATE_RESULT,
        RESET,
        ERROR
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
