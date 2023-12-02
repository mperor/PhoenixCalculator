package eu.mapidev.calculator.phoenix.logic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static eu.mapidev.calculator.phoenix.utils.ArithmeticOperation.ADD;
import static eu.mapidev.calculator.phoenix.utils.CalculatorDigit.*;

class CalculatorEngineTest {

    private CalculatorEngine engine;
    private String actualResult;
    private String actualMemory;

    @BeforeEach
    void createEngineAndSetupTextReadFromResultAndMemory() {
        engine = new CalculatorEngine();
        engine.setOnResultChanged(result -> actualResult = result);
        engine.setOnMemoryChanged(memory -> actualMemory = memory);
    }

    @Test
    void testInitialized_returnsResultEqualsZeroAndEmptyMemory() {
        assertResultEqualsZeroAndMemoryIsEmpty();
    }

    @Test
    void testReset_returnsResultEqualsZeroAndEmptyMemory() {
        engine.reset();
        assertResultEqualsZeroAndMemoryIsEmpty();
    }

    @Test
    void testResetAfterSomeOperations_returnsResultEqualsZeroAndEmptyMemory() {
        engine.inputDigit(_1);
        engine.doOperation(ADD);
        engine.inputDigit(_2);
        engine.calculateResult();
        engine.reset();
        assertResultEqualsZeroAndMemoryIsEmpty();
    }

    @Test
    void testInputSingleDigit_returnsResultEqualsInputDigit() {
        engine.inputDigit(_1);
        Assertions.assertEquals("1", actualResult);
        Assertions.assertEquals("", actualMemory);
    }

    void assertResultEqualsZeroAndMemoryIsEmpty() {
        Assertions.assertEquals("0", actualResult);
        Assertions.assertEquals("", actualMemory);
    }

}