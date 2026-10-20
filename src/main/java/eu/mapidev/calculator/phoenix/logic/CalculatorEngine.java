package eu.mapidev.calculator.phoenix.logic;

import eu.mapidev.calculator.phoenix.utils.ArithmeticOperation;
import eu.mapidev.calculator.phoenix.utils.ResultUtils;

import java.math.BigDecimal;
import java.util.function.Consumer;

public class CalculatorEngine extends BasicCalculatorEngine {

    private Label lblMemory = new Label("");

    public void setOnMemoryChanged(Consumer<String> memoryConsumer) {
        lblMemory.setTextChangedConsumer(memoryConsumer);
    }

    @Override
    public CalculatorEngine reset() {
        super.reset();
        lblMemory.setText("");
        return this;
    }

    @Override
    public CalculatorEngine doOperation(ArithmeticOperation operation) {
        super.doOperation(operation);
        lblMemory.setText(String.format("%s %s", ResultUtils.convertBigDecimalToString(storedResult), operation.getSign()));
        return this;
    }

    @Override
    public CalculatorEngine calculateResult() {
        BigDecimal leftOperand = storedResult;
        super.calculateResult();
        if (storedOperation.isEmpty()) {
            lblMemory.setText(String.format("%s = ", lblResult.getText()));
            return this;
        }

        lblMemory.setText(String.format("%s %s %s =", ResultUtils.convertBigDecimalToString(leftOperand),
                storedOperation.get().getSign(), ResultUtils.convertBigDecimalToString(storedFactor)));
        return this;
    }
}
