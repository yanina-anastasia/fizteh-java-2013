package ru.fizteh.fivt.students.asaitgalin.calc;

import java.io.IOException;

public class Lexer {
    private static enum LexerPrevState {
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
        Token ret = null;
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
            try {
                ret = new Operator(input[cursor++]);
            } catch (IOException ioe) {
                // This can not occure
            }
            prevToken = ret;
        } else {
            StringBuilder sb = new StringBuilder();
            while (cursor < len && !isOperator(input[cursor]) && !Character.isWhitespace(input[cursor])) {
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

    private void skipSpaces() {
        while (cursor != len && Character.isWhitespace(input[cursor])) {
            ++cursor;
        }
    }
}
