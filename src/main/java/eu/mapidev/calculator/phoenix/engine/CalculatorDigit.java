package eu.mapidev.calculator.phoenix.engine;

public enum CalculatorDigit implements CalculatorInput {
    _0, _1, _2, _3, _4, _5, _6, _7, _8, _9;

    @Override
    public String token() {
        return name().substring(1);
    }

    @Override
    public String toString() {
        return token();
    }
}
