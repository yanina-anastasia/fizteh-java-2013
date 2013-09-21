package ru.fizteh.fivt.students.asaitgalin.calc;

public class Lexer {
    private int cursor;
    private char[] input;
    private int len;

    private boolean wasOperator;
    private char prevOperator;

    public Lexer(String s) {
        input = s.toCharArray();
        len = s.length();
    }

    public Token getNext() throws IllegalExpressionException {
        Token ret;
        skipSpaces();
        if (cursor == len) {
            return null;
        }
        if (isOperator(input[cursor])) {
            if (wasOperator) {
                if ((prevOperator == '*' && input[cursor] == '*') || (prevOperator == '/' && input[cursor] == '/') ||
                        (prevOperator == '*' && input[cursor] == '/') || (prevOperator == '/' && input[cursor] == '*')) {
                    throw new IllegalExpressionException();
                }
            }
            wasOperator = true;
            prevOperator = input[cursor];
            ret = new Operator(input[cursor++]);
        } else {
            StringBuilder sb = new StringBuilder();
            while (cursor < len && !isOperator(input[cursor]) && !isSpace(input[cursor])) {
                sb.append(input[cursor++]);
            }
            String out = sb.toString();
            if (out.matches("^[0-9A-I]+$")) {
                ret = new Number(out);
            } else {
                throw new IllegalExpressionException();
            }
            wasOperator = false;
        }
        return ret;
    }

    private boolean isOperator(char c) {
        return (c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')');
    }

    private boolean isSpace(char c) {
        return (c == ' ' || c == '\t');
    }

    private void skipSpaces() {
        while (cursor != len && isSpace(input[cursor])) {
            ++cursor;
        }
    }
}
