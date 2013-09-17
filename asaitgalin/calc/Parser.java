package asaitgalin.calc;

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
        Operator op = Operator.class.cast(look);
        while (!endOfStream && (op.opType == OperatorType.ADDITION || op.opType == OperatorType.SUBTRACTION)) {
            if (op.opType == OperatorType.ADDITION) {
                move();
                x = x + parseTerm();
            } else {
                move();
                x = x - parseTerm();
            }
            op = Operator.class.cast(look);
        }
        return x;
    }

    public int parseTerm() {
        int x = parseUnary();
        Operator op = Operator.class.cast(look);
        while (!endOfStream && (op.opType == OperatorType.MULTIPLICATION || op.opType == OperatorType.DIVISION)) {
            if (op.opType == OperatorType.MULTIPLICATION) {
                move();
                x = x * parseUnary();
            } else {
                move();
                x = x / parseUnary();
            }
            op = Operator.class.cast(look);
        }
        return x;
    }

    public int parseUnary() {
        if (look.type == TokenType.OPERATOR) {
            if (Operator.class.cast(look).opType == OperatorType.SUBTRACTION) {
                move();
                return -parseFactor();
            }
        }
        return parseFactor();
    }

    public int parseFactor() {
        int ret = 0;
        if (look.type == TokenType.NUMBER) {
            ret = asaitgalin.calc.Number.class.cast(look).value;
            move();
        } else if (look.type == TokenType.OPERATOR &&
                Operator.class.cast(look).opType == OperatorType.LBRACKET) {
            move();
            ret = parseExpr();
            // Check matching bracket
            if (look != null && Operator.class.cast(look).opType == OperatorType.RBRACKET) {
                move();
            } else {
                throw new IllegalArgumentException("Wrong bracket balance");
            }
        }
        return ret;
    }
}
