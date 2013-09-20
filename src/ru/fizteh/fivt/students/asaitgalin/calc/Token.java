package ru.fizteh.fivt.students.asaitgalin.calc;

public class Token {

    enum TokenType {
        OPERATOR,
        NUMBER
    }

    public TokenType type;

    public Token(TokenType type) {
        this.type = type;
    }
}
