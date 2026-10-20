package eu.mapidev.calculator.phoenix.engine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static eu.mapidev.calculator.phoenix.engine.ArithmeticOperation.ADD;
import static eu.mapidev.calculator.phoenix.engine.ArithmeticOperation.DIVIDE;
import static eu.mapidev.calculator.phoenix.engine.CalculatorDigit.*;

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

    @Test
    void testOperationOnDecimalNumber_returnsMemoryWithComma() {
        engine.inputDigit(_1).addComma().inputDigit(_5);
        engine.doOperation(ADD);
        Assertions.assertEquals("1,5 +", actualMemory);

        engine.inputDigit(_2);
        engine.calculateResult();
        Assertions.assertEquals("3,5", actualResult);
        Assertions.assertEquals("1,5 + 2 =", actualMemory);
    }

    @Test
    void testRepeatedCalculateResult_returnsMemoryWithPreviousResultAsLeftOperand() {
        engine.inputDigit(_1).doOperation(ADD).inputDigit(_2).calculateResult();
        Assertions.assertEquals("3", actualResult);
        Assertions.assertEquals("1 + 2 =", actualMemory);

        engine.calculateResult();
        Assertions.assertEquals("5", actualResult);
        Assertions.assertEquals("3 + 2 =", actualMemory);
    }

    @Test
    void testOperationOnLargeDivisionResult_returnsMemoryInPlainNotation() {
        engine.inputDigits(_1, _0, _0, _0, _0, _0, _0, _0, _0);
        engine.doOperation(DIVIDE);
        engine.inputDigit(_1);
        engine.doOperation(ADD);
        Assertions.assertEquals("100000000", actualResult);
        Assertions.assertEquals("100000000 +", actualMemory);
    }

    @Test
    void testDivideByZero_returnsErrorResultAndEmptyMemory() {
        engine.inputDigit(_1).doOperation(DIVIDE).inputDigit(_0).calculateResult();
        Assertions.assertEquals(BasicCalculatorEngine.ERROR_RESULT, actualResult);
        Assertions.assertEquals("", actualMemory);

        engine.reset().inputDigit(_1).doOperation(DIVIDE).inputDigit(_0).doOperation(ADD);
        Assertions.assertEquals(BasicCalculatorEngine.ERROR_RESULT, actualResult);
        Assertions.assertEquals("", actualMemory);
    }

    void assertResultEqualsZeroAndMemoryIsEmpty() {
        Assertions.assertEquals("0", actualResult);
        Assertions.assertEquals("", actualMemory);
    }

}