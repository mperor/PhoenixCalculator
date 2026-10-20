package eu.mapidev.calculator.phoenix.engine;

public enum Command implements CalculatorInput {
    COMMA(","),
    CHANGE_SIGN("+/-"),
    PERCENT("%"),
    EQUALS("="),
    CLEAR("C");

    private final String token;

    Command(String token) {
        this.token = token;
    }

    @Override
    public String token() {
        return token;
    }
}
