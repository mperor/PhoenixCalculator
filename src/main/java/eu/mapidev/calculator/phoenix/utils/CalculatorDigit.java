package eu.mapidev.calculator.phoenix.utils;

import eu.mapidev.calculator.phoenix.logic.CalculatorEngine;

import java.util.Arrays;
import java.util.Optional;

public enum CalculatorDigit {
    _0, _1, _2, _3, _4, _5, _6, _7, _8, _9;

    public static Optional<CalculatorDigit> fromString(String digitAsString) {
        return Arrays.stream(values())
                .filter(digit -> digit.toString().equals(digitAsString))
                .findFirst();
    }

    @Override
    public String toString() {
        return name().substring(1);
    }
}
