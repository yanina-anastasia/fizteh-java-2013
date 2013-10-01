package ru.fizteh.students.kamilTalipov.calculator;


public class InputValidator {
    public static boolean isCorrectInput(String[] input) {
        boolean previousInputPartEndIsDigit = false;
        for (String currentPart : input) {
            if (previousInputPartEndIsDigit &&
                !currentPart.isEmpty() &&
                is19BaseDigit(currentPart.charAt(0))) {
                return false;
            }

            if (!currentPart.isEmpty() &&
                is19BaseDigit(currentPart.charAt(currentPart.length() - 1))) {
                previousInputPartEndIsDigit = true;
            } else {
                previousInputPartEndIsDigit = false;
            }

            if (!isOnlyMathSymbols(currentPart)) {
                return false;
            }
        }
        return true;
    }

    public static boolean is19BaseDigit(char c) {
        return (c >= '0' && c <= '9') ||
               (c >= 'A' && c <= 'I');
    }

    public static boolean isMathOperator(char c) {
        return c == '+' || c == '*' || c == '-' || c == '/';
    }

    public static boolean isBrackets(char c) {
        return c == '(' || c == ')';
    }

    private static boolean isOnlyMathSymbols(String string) {
        for (int pos = 0; pos < string.length(); ++pos) {
            if (!is19BaseDigit(string.charAt(pos)) &&
                !isMathOperator(string.charAt(pos)) &&
                !isBrackets(string.charAt(pos)) &&
                string.charAt(pos) != ' ') {
                return false;
            }
        }

        return true;
    }
}
