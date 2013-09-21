package ru.fizteh.fivt.students.dmitryIvanovsky.calculator;
import java.lang.String;

class errorFormula extends Exception {
    public errorFormula(String text) {
        super(text);
    }
}

class myCalc {

    private enum lex {FIRST, NUM, PLUS, MUL, OPEN, CLOSE, END, MINUS, DEL}
    private lex curlex;
    private int it, value, VARIANT;
    private String formula, resFormula;

    public myCalc(String s) {
        formula = s;
        curlex = lex.FIRST;
        it = 0;
        value = 0;
        VARIANT = 17;
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
        if (it >= formula.length()) {
            curlex = lex.END;
            return;
        }

        char c = formula.charAt(it);
        while (c == ' '){
            it += 1;
            c = formula.charAt(it);
        }

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
                    value = 0;
                    while (it < formula.length() && isSuitableSymbol(formula.charAt(it))) {
                        digit = convertToInt(formula.charAt((it)));
                        value = value * VARIANT + digit;
                        it += 1;
                    }
                } else {
                    throw new errorFormula(String.format("Неизвестный символ = %c, номер %d", c, it));
                }
        }
    }

    private int expression() throws errorFormula {
        int first = item();
        while (curlex == lex.PLUS || curlex == lex.MINUS){
            lex tmp = curlex;
            nextLexem();
            int second = item();
            if (tmp == lex.PLUS) {
                first = first + second;
            } else {
                first = first - second;
            }
        }
        return first;
    }

    private int item() throws errorFormula {
        int first = multiplier();
        while (curlex == lex.MUL || curlex == lex.DEL){
            lex tmp = curlex;
            nextLexem();
            int second = multiplier();
            if (tmp == lex.MUL) {
                first = first * second;
            } else {
                if (second == 0) {
                    throw new errorFormula("Деление на ноль");
                }
                first = first / second;

            }
        }
        return first;
    }

    private int multiplier() throws errorFormula {
        int factorRes;
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
                    char c;
                    if (it < formula.length()) {
                        c = formula.charAt(it);
                    } else {
                        c = '?';
                    }
                    throw new errorFormula(String.format("Нарушен балланс скобок, номер = %d, символ %c", it, c));
                }
                break;
            default:
                char c;
                if (it < formula.length()) {
                    c = formula.charAt(it);
                } else {
                    c = formula.charAt(it-1);
                }
                throw new errorFormula(String.format("Неверная последовательность символов, номер %d, символ %c", it, c));
        }
        return factorRes;
    }

    public String castNumeralSystem(int n) {
        if (n == 0) {
            return "0";
        }
        int sign = 1;
        if (n < 0) {
            sign = -1;
        }
        n *= sign;
        String res = "";
        while (n != 0) {
            int modulo = n % VARIANT;
            if (modulo <= 9) {
                res += (char) ('0' + modulo);
            } else {
                res += (char) ('A' + modulo - 10);
            }
            n = n / VARIANT;
        }
        String reverse = new StringBuffer(res).reverse().toString();
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
            throw new errorFormula("Проблема с концом");
        }
        resFormula = res;
        return resFormula;
    }
}

public class Calculator {

    public static void main(String[] args) {
        //String query = "1+5+8";
        StringBuilder builder = new StringBuilder();
        for (String arg : args) {
            builder.append(arg);
        }
        String query = builder.toString();
        if (query.equals("")) {
            System.out.println("Пустой ввод");
            return;
        }
        try {
            myCalc calculator = new myCalc(query);
            String res = calculator.result();
            System.out.println(res);
        } catch (errorFormula e) {
            System.out.println(e);
        }

    }

}

