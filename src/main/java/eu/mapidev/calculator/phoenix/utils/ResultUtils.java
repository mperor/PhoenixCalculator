package eu.mapidev.calculator.phoenix.utils;

import java.math.BigDecimal;

public class ResultUtils {

    public static final String DOT = ".";
    public static final String COMMA = ",";

    public static BigDecimal safeConvertStringToBigDecimal(String string) {
        try {
            string = replaceCommaWithDot(string);
            return new BigDecimal(simplifyStringBeforeConvert(string));
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    public static String convertBigDecimalToString(BigDecimal bigDecimal) {
        String string = bigDecimal.toString();
        if (string.contains(DOT))
            string = removeZeroesInFractionalPart(string);

        return removeDotIfLastCharacter(string).replace(DOT, COMMA);
    }

    private static String replaceCommaWithDot(String string) {
        return string.replace(COMMA, DOT);
    }

    private static String simplifyStringBeforeConvert(String string) {
        string = removeDotIfLastCharacter(string);
        if (string.contains(DOT))
            string = removeZeroesInFractionalPart(string);

        return string;
    }

    private static String removeDotIfLastCharacter(String string) {
        if (string.endsWith(DOT))
            return string.substring(0, string.length() - 1);

        return string;
    }

    private static String removeZeroesInFractionalPart(String string) {
        String[] numberParts = string.split("\\.");
        String fractionalPart = numberParts[1];
        while (fractionalPart.endsWith("0")) {
            fractionalPart = fractionalPart.substring(0, fractionalPart.length() - 1);
        }
        return String.format("%s.%s", numberParts[0], fractionalPart);
    }

}
