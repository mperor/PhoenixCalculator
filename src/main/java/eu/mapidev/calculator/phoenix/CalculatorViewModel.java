package eu.mapidev.calculator.phoenix;

import eu.mapidev.calculator.phoenix.engine.CalculatorEngine;
import eu.mapidev.calculator.phoenix.engine.CalculatorInput;
import eu.mapidev.calculator.phoenix.engine.CalculatorState;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;

/**
 * Exposes the calculator state as observable values the view can bind to.
 * Uses only javafx.base, so it works (and is tested) without a running JavaFX toolkit.
 */
public class CalculatorViewModel {

    private final ReadOnlyObjectWrapper<CalculatorState> state = new ReadOnlyObjectWrapper<>(CalculatorState.initial());
    private final ObservableValue<String> result = state.map(CalculatorState::display);
    private final ObservableValue<String> memory = state.map(CalculatorState::memory);

    public void accept(CalculatorInput input) {
        state.set(CalculatorEngine.apply(state.get(), input));
    }

    public ReadOnlyObjectProperty<CalculatorState> stateProperty() {
        return state.getReadOnlyProperty();
    }

    public ObservableValue<String> resultProperty() {
        return result;
    }

    public ObservableValue<String> memoryProperty() {
        return memory;
    }
}
