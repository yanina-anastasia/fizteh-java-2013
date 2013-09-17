package asaitgalin.calc;

enum OperatorType {
    ADDITION,
    SUBTRACTION,
    MULTIPLICATION,
    DIVISION,
    LBRACKET,
    RBRACKET
}

public class Operator extends Token {
    public OperatorType opType;

    public Operator(char c) {
        super(TokenType.OPERATOR);
        switch (c) {
            case '+':
                opType = OperatorType.ADDITION;
                break;
            case '-':
                opType = OperatorType.SUBTRACTION;
                break;
            case '*':
                opType = OperatorType.MULTIPLICATION;
                break;
            case '/':
                opType = OperatorType.DIVISION;
                break;
            case '(':
                opType = OperatorType.LBRACKET;
                break;
            case ')':
                opType = OperatorType.RBRACKET;
                break;
        }
    }
}
