package asaitgalin.calc;

public class Lexer {
    private int cursor;
    private char[] input;
    private int len;

    public Lexer(String s) {
        input = s.toCharArray();
        len = s.length();
    }

    public Token getNext() throws IllegalArgumentException {
        Token ret = null;
        skipSpaces();
        if (cursor == len) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if (isOperator(input[cursor])) {
            ret = new Operator(input[cursor++]);
        } else {
            while (cursor < len && !isOperator(input[cursor]) && !isSpace(input[cursor])) {
                sb.append(input[cursor++]);
            }
            String out = sb.toString();
            if (out.matches("^[0-9A-I]+$")) {
                ret = new Number(sb.toString());
            } else {
                throw new IllegalArgumentException("Illegal input string");
            }
            sb.setLength(0);
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
