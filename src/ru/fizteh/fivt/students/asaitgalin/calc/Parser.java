package ru.fizteh.fivt.students.asaitgalin.calc;

public class Parser {
    private Lexer scanner;
    private Token look;
    private boolean endOfStream;

    public Parser(Lexer lexer) {
        scanner = lexer;
        move();
    }

    public void move() {
        look = scanner.getNext();
        endOfStream = look == null;
    }

    public int parseExpr() {
        int x = parseTerm();
        Operator op = (Operator)look;
        while (!endOfStream && (op.opType == Operator.OperatorType.ADDITION || op.opType == Operator.OperatorType.SUBTRACTION)) {
            if (op.opType == Operator.OperatorType.ADDITION) {
                move();
                x = x + parseTerm();
            } else {
                move();
                x = x - parseTerm();
            }
            op = (Operator)look;
        }
        return x;
    }

    public int parseTerm() {
        int x = parseUnary();
        Operator op = (Operator)look;
        while (!endOfStream && (op.opType == Operator.OperatorType.MULTIPLICATION || op.opType == Operator.OperatorType.DIVISION)) {
            if (op.opType == Operator.OperatorType.MULTIPLICATION) {
                move();
                x = x * parseUnary();
            } else {
                move();
                x = x / parseUnary();
            }
            op = (Operator)look;
        }
        return x;
    }

    public int parseUnary() {
        if (look.type == Token.TokenType.OPERATOR) {
            if (((Operator)look).opType == Operator.OperatorType.SUBTRACTION) {
                move();
                return -parseFactor();
            }
        }
        return parseFactor();
    }

    public int parseFactor() {
        int ret = 0;
        if (look.type == Token.TokenType.NUMBER) {
            ret = ((Number)look).value;
            move();
        } else if (look.type == Token.TokenType.OPERATOR && ((Operator)look).opType == Operator.OperatorType.LBRACKET) {
            move();
            ret = parseExpr();
            // Check matching bracket
            if (look != null && ((Operator)look).opType == Operator.OperatorType.RBRACKET) {
                move();
            } else {
                throw new IllegalArgumentException("Wrong bracket balance");
            }
        }
        return ret;
    }
}
