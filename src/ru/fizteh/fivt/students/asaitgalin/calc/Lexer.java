package ru.fizteh.fivt.students.asaitgalin.calc;

public class Lexer {
    private enum LexerPrevState {
        OPERATOR,
        NUMBER
    }

    private int cursor;
    private char[] input;
    private int len;

    private LexerPrevState prevState;
    private Token prevToken;

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
            if (prevState == LexerPrevState.OPERATOR) {
                Operator op = (Operator)prevToken;
                if ((op.lexeme == '*' && input[cursor] == '*') || (op.lexeme == '/' && input[cursor] == '/') ||
                        (op.lexeme == '*' && input[cursor] == '/') || (op.lexeme == '/' && input[cursor] == '*')) {
                    throw new IllegalExpressionException();
                }
            }
            prevState = LexerPrevState.OPERATOR;
            ret = new Operator(input[cursor++]);
            prevToken = ret;
        } else {
            StringBuilder sb = new StringBuilder();
            while (cursor < len && !isOperator(input[cursor]) && !isSpace(input[cursor])) {
                sb.append(input[cursor++]);
            }
            String out = sb.toString();
            if (out.matches("^[0-9A-I]+$")) {
                if (prevState == LexerPrevState.NUMBER) {
                    throw new IllegalExpressionException();
                }
                ret = new Number(out);
                prevToken = ret;
            } else {
                throw new IllegalExpressionException();
            }
            prevState = LexerPrevState.NUMBER;
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
