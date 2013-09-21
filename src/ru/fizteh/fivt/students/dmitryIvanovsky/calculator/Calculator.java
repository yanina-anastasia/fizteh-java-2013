package ru.fizteh.fivt.students.dmitryIvanovsky.calculator;

import java.math.BigInteger;

class errorFormula extends Exception {
    public errorFormula(String text) {
        super(text);
    }
}

class MyCalc {

    private enum lex {FIRST, NUM, PLUS, MUL, OPEN, CLOSE, END, MINUS, DEL}
    private lex curlex;
    private int it;
    private BigInteger value;
    private int variant;
    private String formula, resFormula;

    public MyCalc(String s) {
        formula = s;
        curlex = lex.FIRST;
        it = 0;
        value = BigInteger.valueOf(0);
        variant = 17;
    }

    public boolean isSuitableSymbol(char c) {
        return ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'G'));
    }

    public int convertToInt(char c) {
        if (c >= 'A' && c <= 'G') {
            return c - 'A' + 10;
        } else {
            return c - '0';
        }
    }

    private void nextLexem() throws errorFormula {
        while (it < formula.length() && formula.charAt(it) == ' ') {
            it += 1;
        }

        if (it >= formula.length()) {
            curlex = lex.END;
            return;
        }
        char c = formula.charAt(it);

        switch (c) {
            case '(':
                curlex = lex.OPEN;
                it += 1;
                break;
            case ')':
                curlex = lex.CLOSE;
                it += 1;
                break;
            case '+':
                curlex = lex.PLUS;
                it += 1;
                break;
            case '-':
                curlex = lex.MINUS;
                it += 1;
                break;
            case '*':
                curlex = lex.MUL;
                it += 1;
                break;
            case '/':
                curlex = lex.DEL;
                it += 1;
                break;
            default:
                if (isSuitableSymbol(c)) {
                    int digit;
                    curlex = lex.NUM;
                    value = BigInteger.valueOf(0);
                    while (it < formula.length() && isSuitableSymbol(formula.charAt(it))) {
                        digit = convertToInt(formula.charAt((it)));
                        value = value.multiply(BigInteger.valueOf(variant));
                        value = value.add(BigInteger.valueOf(digit));
                        it += 1;
                    }
                } else {
                    String error = String.format("Неизвестный символ '%c' в \"%s\"= , номер %d", c, formula, it);
                    throw new errorFormula(error);
                }
        }
    }

    private BigInteger expression() throws errorFormula {
        BigInteger first = item();
        while (curlex == lex.PLUS || curlex == lex.MINUS){
            lex tmp = curlex;
            nextLexem();
            BigInteger second = item();
            if (tmp == lex.PLUS) {
                first = first.add(second);
            } else {
                first = first.subtract(second);
            }
        }
        return first;
    }

    private BigInteger item() throws errorFormula {
        BigInteger first = multiplier();
        while (curlex == lex.MUL || curlex == lex.DEL){
            lex tmp = curlex;
            nextLexem();
            BigInteger second = multiplier();
            if (tmp == lex.MUL) {
                first = first.multiply(second);
            } else {
                if (second.equals(BigInteger.valueOf(0))) {
                    throw new errorFormula(String.format("Деление на ноль в \"%s\", номер %d ", formula, it));
                }
                first = first.divide(second);

            }
        }
        return first;
    }

    private BigInteger multiplier() throws errorFormula {
        BigInteger factorRes;
        switch (curlex) {
            case NUM:
                factorRes = value;
                nextLexem();
                break;
            case OPEN:
                nextLexem();
                factorRes = expression();
                if (curlex == lex.CLOSE) {
                    nextLexem();
                } else {
                    String error = String.format("Нарушен балланс скобок в \"%s\", номер %d", formula, it);
                    throw new errorFormula(error);
                }
                break;
            default:
                char c;
                if (it < formula.length()) {
                    c = formula.charAt(it);
                } else {
                    c = formula.charAt(it-1);
                }
                String error = String.format("Неверное выражение \"%s\", номер %d, символ \'%c\'", formula, it, c);
                throw new errorFormula(error);
        }
        return factorRes;
    }

    public String castNumeralSystem(BigInteger n) {
        if (n.equals(BigInteger.valueOf(0))) {
            return "0";
        }
        int sign = 1;
        if (n.compareTo(BigInteger.valueOf(0)) < 0) {
            sign = -1;
            n = n.negate();
        }

        String res = "";
        while (!n.equals(BigInteger.valueOf(0))) {
            int modulo = n.remainder(BigInteger.valueOf(variant)).intValue();
            if (modulo <= 9) {
                res += (char) ('0' + modulo);
            } else {
                res += (char) ('A' + modulo - 10);
            }
            n = n.divide(BigInteger.valueOf(variant));
        }
        String reverse = new StringBuilder(res).reverse().toString();
        if (sign < 0) {
            return '-' + reverse;
        } else {
            return  reverse;
        }
    }

    public String result() throws errorFormula {
        if (curlex == lex.END){
            return resFormula;
        }
        nextLexem();
        String res = castNumeralSystem(expression());
        if (curlex != lex.END){
            char c;
            if (it < formula.length()) {
                c = formula.charAt(it);
            } else {
                c = formula.charAt(it-1);
            }
            String error = String.format("Неверное выражение \"%s\", номер %d, символ \'%c\'", formula, it, c);
            throw new errorFormula(error);
        }
        resFormula = res;
        return resFormula;
    }
}

public class Calculator {

    public static int main(String[] args) {
        StringBuilder builder = new StringBuilder();
        for (String arg : args) {
            builder.append(arg);
            builder.append(' ');
        }
        String query = builder.toString();
        if (query.equals("")) {
            System.out.println("Пустой ввод");
            return 1;
        }
        try {
            MyCalc calculator = new MyCalc(query);
            String res = calculator.result();
            System.out.println(res);
        } catch (errorFormula e) {
            System.out.println(e);
        }

        return 0;
    }

}

