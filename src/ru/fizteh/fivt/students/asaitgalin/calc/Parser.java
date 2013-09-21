package ru.fizteh.fivt.students.asaitgalin.calc;

public class Parser {
    private Lexer scanner;
    private Token look;
    private boolean endOfStream;

    public Parser(Lexer lexer) throws IllegalExpressionException {
        scanner = lexer;
        move();
    }

    public void move() throws IllegalExpressionException {
        look = scanner.getNext();
        endOfStream = look == null;
    }

    public int parseExpr() throws IllegalExpressionException {
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

    public int parseTerm() throws IllegalExpressionException {
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

    public int parseUnary() throws IllegalExpressionException {
        if (endOfStream) {
            throw new IllegalExpressionException();
        }
        if (look.type == Token.TokenType.OPERATOR) {
            if (((Operator)look).opType == Operator.OperatorType.SUBTRACTION) {
                move();
                return -parseFactor();
            }
        }
        return parseFactor();
    }

    public int parseFactor() throws IllegalExpressionException {
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
                throw new IllegalExpressionException("Missing matching bracket");
            }
        }
        return ret;
    }
}
