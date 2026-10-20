package eu.mapidev.calculator.phoenix;

import eu.mapidev.calculator.phoenix.engine.ArithmeticOperation;
import eu.mapidev.calculator.phoenix.engine.CalculatorDigit;
import eu.mapidev.calculator.phoenix.engine.Command;
import javafx.beans.property.SimpleStringProperty;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculatorViewModelTest {

    private final CalculatorViewModel viewModel = new CalculatorViewModel();

    @Test
    void shouldStartWithZeroAndEmptyMemory() {
        assertEquals("0", viewModel.resultProperty().getValue());
        assertEquals("", viewModel.memoryProperty().getValue());
    }

    @Test
    void shouldUpdateBoundPropertiesOnInput() {
        var result = new SimpleStringProperty();
        var memory = new SimpleStringProperty();
        result.bind(viewModel.resultProperty());
        memory.bind(viewModel.memoryProperty());

        viewModel.accept(CalculatorDigit._1);
        viewModel.accept(ArithmeticOperation.ADD);
        viewModel.accept(CalculatorDigit._2);
        viewModel.accept(Command.EQUALS);

        assertEquals("3", result.get());
        assertEquals("1 + 2 =", memory.get());
    }

    @Test
    void shouldNotifyResultListenersOnlyWhenResultChanges() {
        var results = new ArrayList<String>();
        viewModel.resultProperty().addListener((observable, oldValue, newValue) -> results.add(newValue));

        viewModel.accept(CalculatorDigit._5);
        viewModel.accept(ArithmeticOperation.ADD);
        viewModel.accept(CalculatorDigit._5);
        viewModel.accept(Command.EQUALS);

        assertEquals(List.of("5", "10"), results);
    }
}
