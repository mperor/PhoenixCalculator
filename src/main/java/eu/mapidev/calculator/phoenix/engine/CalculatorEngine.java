package eu.mapidev.calculator.phoenix.engine;

import eu.mapidev.calculator.phoenix.engine.ArithmeticOperation;
import eu.mapidev.calculator.phoenix.engine.DisplayFormat;

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
        if (isError())
            return this;

        lblMemory.setText(String.format("%s %s", DisplayFormat.format(storedResult), operation.getSign()));
        return this;
    }

    @Override
    public CalculatorEngine calculateResult() {
        BigDecimal leftOperand = storedResult;
        super.calculateResult();
        if (isError())
            return this;

        if (storedOperation.isEmpty()) {
            lblMemory.setText(String.format("%s = ", lblResult.getText()));
            return this;
        }

        lblMemory.setText(String.format("%s %s %s =", DisplayFormat.format(leftOperand),
                storedOperation.get().getSign(), DisplayFormat.format(storedFactor)));
        return this;
    }
}
