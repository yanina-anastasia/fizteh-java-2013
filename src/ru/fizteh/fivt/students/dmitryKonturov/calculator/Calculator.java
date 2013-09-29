package ru.fizteh.fivt.students.dmitryKonturov.calculator;

import java.math.BigInteger;

/**
 *      Калькулятор, считающий в 17-ичной системе счисления.
 *      Все символы переводятся в верхний регистр.
 *      Допустимые опереции:
 *          бинарные: +, -, *, /
 *          объединение операций круглами скобками.
 */

class WrongExpression extends Exception {
    private String errorDescription;

    WrongExpression(String err) {
        errorDescription = err;
    }

    @Override
    public String toString() {
        return ("Неправильное выражение: " + errorDescription);
    }
}

class MyCalculator {
    private final BigInteger NUMBER_BASE = BigInteger.valueOf(17);
    private String expression;
    private int curPosition;
    private Lexeme curLexeme;
    private BigInteger curNumber;

    enum Lexeme {
        PLUS, MINUS, DIV, MUL, OPEN_BR, CLOSE_BR, NUMBER, END
    }


    private boolean isDigit(char c) {
        return ('0' <= c && c <= '9') || ('A' <= c && c <= 'G');
    }

    private long getDigit(char c) {
        return ('0' <= c && c <= '9') ? (long) (c - '0') : (long) (c - 'A' + 10);
    }

    private Lexeme getLexeme() throws WrongExpression {
        while (curPosition < expression.length() && Character.isWhitespace(expression.charAt(curPosition))) {
            ++curPosition;
        }

        if (curPosition >= expression.length()) {
            return Lexeme.END;
        }

        char c = expression.charAt(curPosition);

        switch (c) {
            case '+':
                ++curPosition;
                return Lexeme.PLUS;

            case '-':
                ++curPosition;
                return Lexeme.MINUS;

            case '/':
                ++curPosition;
                return Lexeme.DIV;

            case '*':
                ++curPosition;
                return Lexeme.MUL;

            case '(':
                ++curPosition;
                return Lexeme.OPEN_BR;

            case ')':
                ++curPosition;
                return Lexeme.CLOSE_BR;

            default:
                if (isDigit(c)) {
                    curNumber = BigInteger.ZERO;
                    while (isDigit(c)) {
                        curNumber = curNumber.multiply(NUMBER_BASE);
                        curNumber = curNumber.add(BigInteger.valueOf(getDigit(c)));
                        ++curPosition;
                        if (curPosition >= expression.length()) {
                            break;
                        }
                        c = expression.charAt(curPosition);
                    }
                    return Lexeme.NUMBER;
                } else {
                    String error = String.format("Неизвестный символ %c в позиции %d.", c, curPosition);
                    throw new WrongExpression(error);
                }
        }

    }

    private void nextLexeme() throws WrongExpression {
        curLexeme = getLexeme();
    }

    private BigInteger calculateExpression(boolean isInBrackets) throws WrongExpression {
        BigInteger result = calculateSummand();
        BigInteger tmp;
        while (true) {
            switch (curLexeme) {
                case END:
                    return result;

                case CLOSE_BR:
                    if (isInBrackets) {
                        return result;
                    } else {
                        throw new WrongExpression(String.format("Нарушен баланс скобок в позиции %d.", curPosition));
                    }

                case PLUS:
                    nextLexeme();
                    tmp = calculateSummand();
                    result = result.add(tmp);
                    break;

                case MINUS:
                    nextLexeme();
                    tmp = calculateSummand();
                    result = result.subtract(tmp);
                    break;

                default:
                    throw new WrongExpression(String.format("Нельзя выделить слагаемое в %d.", curPosition));

            }
        }
    }

    private BigInteger calculateSummand() throws WrongExpression {
        BigInteger result = calculateMultiplier();
        BigInteger tmp;
        while (true) {
            switch (curLexeme) {
                case END:
                    return result;

                case MUL:
                    nextLexeme();
                    tmp = calculateMultiplier();
                    result = result.multiply(tmp);
                    break;

                case DIV:
                    nextLexeme();
                    tmp = calculateMultiplier();
                    result = result.divide(tmp);
                    break;

                default:
                    return result;
            }
        }
    }

    private BigInteger calculateMultiplier() throws WrongExpression {
        switch (curLexeme) {
            case END:
                throw new WrongExpression("Найден конец выражения, однако ожидался множитель.");

            case NUMBER:
                nextLexeme();
                return curNumber;

            case MINUS:
                throw new WrongExpression("Унарный минус не поддерживаается.");

            case PLUS:
                throw new WrongExpression("Унарный плюс не поддерживается.");

            case OPEN_BR:
                nextLexeme();
                BigInteger result = calculateExpression(true);
                if (curLexeme == Lexeme.CLOSE_BR) {
                    nextLexeme();
                    return result;
                } else {
                    throw new WrongExpression(String.format("Нарушен баланс скобок в позиции %d.", curPosition));
                }

            case CLOSE_BR:
                throw new WrongExpression(String.format("Ожидалось непустое выражение в скобках или число в позиции %d.",
                                                        curPosition));

            default:
                throw new WrongExpression(String.format("Ожидалось выражение в скобках или число в позиции %d, " +
                                                        "найдено %s. Возможно записаны две операции подряд.",
                                                        curPosition, curLexeme.toString()));
        }
    }

    private char digitToChar(int digit) {
        if (digit < 10) {
            return (char) ('0' + digit);
        } else {
            return (char) ('A' + digit - 10);
        }
    }

    private String changeBase(BigInteger bigNum) {
        StringBuilder builder = new StringBuilder();
        do {
            builder.append(digitToChar(bigNum.mod(NUMBER_BASE).intValue()));
            bigNum = bigNum.divide(NUMBER_BASE);
        } while (!bigNum.equals(BigInteger.ZERO));
        return builder.reverse().toString();
    }

    public String calculate(String expr) throws WrongExpression {
        expression = expr;
        curPosition = 0;
        curLexeme = getLexeme();
        if (curLexeme == Lexeme.END) {
            throw new WrongExpression("Пустое выражение.");
        }
        String result = changeBase(calculateExpression(false));
        if (curLexeme != Lexeme.END) {
            throw new WrongExpression(String.format("Несколько последних символов были проигнорированы.\n" +
                                      "Результат без них равен: %s.", result));
        }
        return result;
    }

}

public class Calculator {

    public static void main(String[] args) {
        StringBuilder argsBuilder = new StringBuilder();

        for (String arg : args) {
            argsBuilder.append(arg);
        }

        String argsString = argsBuilder.toString().toUpperCase();

        try {
            MyCalculator calc = new MyCalculator();
            String result = calc.calculate(argsString);
            System.out.println(result);
        } catch (WrongExpression e) {
            System.err.println(e);
        } catch (ArithmeticException e) {
            System.err.println(e);
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
