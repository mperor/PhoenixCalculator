package eu.mapidev.calculator.phoenix.engine;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DisplayFormatTest {

    @Test
    void shouldSafeConvertStringToBigDecimal() {
        assertEquals(BigDecimal.ZERO, DisplayFormat.parse("0,"));
        assertEquals(BigDecimal.ONE, DisplayFormat.parse("1,00"));
        assertEquals(new BigDecimal("13.105"), DisplayFormat.parse("13,1050"));
        assertEquals(BigDecimal.ZERO, DisplayFormat.parse("NOT A NUMBER"));
    }

    @Test
    void shouldConvertBigDecimalToString(){
        assertEquals("0,123", DisplayFormat.format(new BigDecimal("0.123")));
        assertEquals("10", DisplayFormat.format(BigDecimal.TEN));
        assertEquals("0", DisplayFormat.format(BigDecimal.ZERO));
        assertEquals("12", DisplayFormat.format(new BigDecimal("12.00")));
        assertEquals("12,1", DisplayFormat.format(new BigDecimal("12.1000")));
    }

    @Test
    void shouldConvertBigDecimalToPlainStringWithinDisplayRange() {
        assertEquals("100000000", DisplayFormat.format(new BigDecimal("1.000000E+8")));
        assertEquals("1234567890123456", DisplayFormat.format(new BigDecimal("1.234567890123456E+15")));
        assertEquals("-2500", DisplayFormat.format(new BigDecimal("-2.5E+3")));
        assertEquals("0,0000001", DisplayFormat.format(new BigDecimal("1E-7")));
    }

    @Test
    void shouldConvertBigDecimalToScientificStringOutsideDisplayRange() {
        assertEquals("1E+16", DisplayFormat.format(new BigDecimal("10000000000000000")));
        assertEquals("1,5E+20", DisplayFormat.format(new BigDecimal("1.5E+20")));
        assertEquals("1E-8", DisplayFormat.format(new BigDecimal("0.00000001")));
        assertEquals("-2,5E-9", DisplayFormat.format(new BigDecimal("-2.5E-9")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-12.5", "100000000", "9999999999999999", "0.0000001", "1E+20", "-1.5E-9"})
    void shouldConvertBigDecimalToStringAndBackWithoutChangingValue(String value) {
        BigDecimal decimal = new BigDecimal(value);
        String displayed = DisplayFormat.format(decimal);

        assertEquals(0, decimal.compareTo(DisplayFormat.parse(displayed)),
                () -> value + " displayed as " + displayed);
    }
}