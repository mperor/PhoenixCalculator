package eu.mapidev.calculator.phoenix.logic;

import eu.mapidev.calculator.phoenix.utils.ArithmeticOperation;
import eu.mapidev.calculator.phoenix.utils.CalculatorDigit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static eu.mapidev.calculator.phoenix.utils.ArithmeticOperation.ADD;
import static eu.mapidev.calculator.phoenix.utils.CalculatorDigit.*;

class BasicCalculatorEngineTest {

    private BasicCalculatorEngine engine;
    private String actualResult;

    @BeforeEach
    void createEngineAndSetupReadTextFromResult() {
        engine = new CalculatorWithPrinter();
        engine.setOnResultChanged(result -> actualResult = result);
    }

    @Test
    void testInitializedCalculator_returnsResultEqualsZero() {
        assertResultEquals("0");
    }

    @Test
    void testReset_returnsResultEqualsZero() {
        engine.reset();
        assertResultEquals("0");
    }

    @Test
    void testResetAfterSomeOperations_returnsResultEqualsZero() {
        engine.inputDigit(_2);
        engine.doOperation(ADD);
        engine.inputDigit(_2);
        engine.reset();
        assertResultEquals("0");
    }

    @Test
    void testInputDigits_returnNumberWithDigits() {
        engine.inputDigits(_1, _2, _3, _4, _5, _6, _7, _8, _9);
        assertResultEquals("123456789");
    }

    @Test
    void testInputSomeZerosOnStartAndAfterDigit_returnsResultWithIgnoredZeroesOnStart() {
        engine.inputDigits(_0, _0, _0, _1, _0, _0, _0);
        assertResultEquals("1000");
    }

    @Test
    void testAddCommaWithInputDigitsAndCalculateResultCombination() {
        engine.inputDigit(_1).addComma().inputDigits(_0, _0, _1);
        assertResultEquals("1,001");

        engine.reset().addComma().inputDigits(_1, _0, _0);
        assertResultEquals("0,100");
        engine.calculateResult();
        assertResultEquals("0,1");

        engine.reset().addComma().addComma().inputDigit(_0);
        assertResultEquals("0,0");
        engine.calculateResult();
        assertResultEquals("0");
    }

    @Test
    void testArithmeticOperationsWithCalculateResultCombination() {
        engine.inputDigit(_2).add().inputDigits(_2).calculateResult();
        assertResultEquals("4");

        engine.reset().inputDigit(_2).add().inputDigit(_2).add();
        assertResultEquals("4");
        engine.calculateResult().calculateResult();
        assertResultEquals("12");

        engine.reset().inputDigit(_2).subtract().inputDigits(_2).calculateResult();
        assertResultEquals("0");

        engine.reset().inputDigit(_2).multiply().inputDigits(_2).calculateResult();
        assertResultEquals("4");

        engine.reset().inputDigit(_2).divide().inputDigits(_2).calculateResult();
        assertResultEquals("1");
    }

    private void assertResultEquals(String expected) {
        System.out.print(" ⇒ " + actualResult);
        Assertions.assertEquals(expected, actualResult);
        System.out.println();
    }

    private static class CalculatorWithPrinter extends BasicCalculatorEngine {

        public CalculatorWithPrinter() {
            System.out.print("I");
        }

        @Override
        public BasicCalculatorEngine inputDigit(CalculatorDigit digit) {
            super.inputDigit(digit);
            System.out.print("⇾");
            System.out.print(digit);
            return this;
        }

        @Override
        public BasicCalculatorEngine changeSign() {
            super.changeSign();
            System.out.print("⇾");
            System.out.print("+/-");
            return this;
        }

        @Override
        public BasicCalculatorEngine reset() {
            super.reset();
            System.out.print("⇾");
            System.out.print("R");
            return this;
        }

        @Override
        public BasicCalculatorEngine addComma() {
            super.addComma();
            System.out.print("⇾");
            System.out.print(",");
            return this;
        }

        @Override
        public BasicCalculatorEngine doPercent() {
            super.doPercent();
            System.out.print("⇾");
            System.out.print("%");
            return this;
        }

        @Override
        public BasicCalculatorEngine doOperation(ArithmeticOperation operation) {
            super.doOperation(operation);
            System.out.print("⇾");
            System.out.print(operation.getSign());
            return this;
        }

        @Override
        public BasicCalculatorEngine calculateResult() {
            var beforeCalculating = lblResult.getText();
            super.calculateResult();
            System.out.printf("⇾CR[%s = %s]", beforeCalculating, lblResult.getText());
            return this;
        }
    }
}