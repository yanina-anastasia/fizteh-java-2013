package asaitgalin.calc;

enum TokenType {
    OPERATOR,
    NUMBER
}

public class Token {
    public TokenType type;

    public Token(TokenType type) {
        this.type = type;
    }
}
