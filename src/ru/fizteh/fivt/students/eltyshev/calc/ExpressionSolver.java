package ru.fizteh.fivt.students.eltyshev.calc;

public class ExpressionSolver {
    private String Expression;
    private Node ExpressionTree;
    private static final int Radix = 17;
    private static String DigitExpression;

    static {
        MakeDigitExpression();
    }

    public double Result;

    public ExpressionSolver(String expression) {
        Expression = expression;
    }

    private static void MakeDigitExpression() {
        StringBuilder sb = new StringBuilder("^-?[0-9a-");
        char endChar = (char) ('a' + Radix - 11);
        sb.append(endChar);
        sb.append("]+$");
        DigitExpression = sb.toString();
    }

    public static double Execute(String expression) {
        ExpressionSolver solver = new ExpressionSolver(expression);
        if (!solver.Calculate())
            throw new IllegalArgumentException();
        return solver.Result;
    }

    public boolean Calculate() {
        Expression = Expression.toLowerCase();
        if (!CheckBracets(Expression)) {
            throw new BadBracketBalanceException();
        }

        ExpressionTree = MakeTree(Expression);
        ExecuteTree(ExpressionTree);
        Result = ExpressionTree.Value;
        return true;
    }

    private void ExecuteTree(Node tree) {
        if (tree == null)
            return;

        ExecuteTree(tree.Left);
        ExecuteTree(tree.Right);

        switch (tree.Type) {
            case NUMBER:
                tree.Value = MakeNumber(tree.Operation);
                break;
            case OPERATION:
                tree.Value = ExecuteExpression(tree.Operation, tree.Left.Value, tree.Right.Value);
                break;
        }
    }

    private Double MakeNumber(String expression) {
        long result = Long.parseLong(expression, Radix);
        return (double) result;
    }

    private Node MakeTree(String ex) {
        ex = FormatExpression(ex);
        Node res = new Node();
        if (CheckNumber(ex)) {
            res.Operation = ex;
            res.Left = null;
            res.Right = null;
            res.Type = ExpressionType.NUMBER;
            return res;
        }

        int signIndex = FindOperationSign(ex);
        if (signIndex != -1) {
            res.Operation = ex.substring(signIndex, signIndex + 1);
            res.Left = MakeTree(ex.substring(0, signIndex));
            res.Right = MakeTree(ex.substring(signIndex + 1));
            res.Type = ExpressionType.OPERATION;
            return res;
        }

        throw new InvalidCharacterException(ex);
    }

    public static String FormatExpression(String expression) {
        expression = expression.trim();
        if (expression.charAt(0) != '(')
            return expression;
        int balance = 1;
        for (int index = 1; index < expression.length(); ++index) {
            if (expression.charAt(index) == ')') {
                balance -= 1;
            }
            if (expression.charAt(index) == '(') {
                balance += 1;
            }
            if (balance == 0 && index != expression.length() - 1)
                return expression;
        }
        return expression.substring(1, expression.length() - 1);
    }

    public boolean CheckNumber(String expression) {
        return expression.matches(DigitExpression);
    }

    public static int FindOperationSign(String expression) {
        char[][] signs = {new char[]{'-', '+'},
                new char[]{'*', '/'},
                new char[]{'^'}
        };
        for (int i = 0; i < signs.length; ++i) {
            int balance = 0;
            for (int j = expression.length() - 1; j >= 0; j--) {
                char c = expression.charAt(j);
                if (c == '(')
                    balance++;
                if (c == ')')
                    balance--;
                if (ContainsValue(signs[i], c) && balance == 0 && j > 0 && CheckPrevious(expression.substring(j - 1, j))) {
                    return j;
                }
            }
        }
        return -1;
    }

    private static boolean ContainsValue(char[] array, char value) {
        for (char c : array)
            if (c == value)
                return true;
        return false;
    }

    private static boolean CheckPrevious(String previous) {
        return previous.matches(DigitExpression) || previous.matches("^[\\)]$") || previous.equals(" ");
    }

    public static boolean CheckBracets(String ex) {
        int balance = 0;
        for (int i = 0; i < ex.length() && balance > -1; i++) {
            if (ex.charAt(i) == '(')
                balance++;
            if (ex.charAt(i) == ')')
                balance--;
        }
        return balance == 0;
    }

    public static double ExecuteExpression(String operation, double arg1, double arg2) {
        double result = 0.0;
        if (operation.equals("+")) {
            result = arg1 + arg2;
        } else if (operation.equals("-")) {
            result = arg1 - arg2;
        } else if (operation.equals("*")) {
            result = arg1 * arg2;
        } else if (operation.equals("/")) {
            if (Double.compare(Math.abs(arg2), 0.0) == 0)
                result = Double.NaN;
            else
                result = arg1 / arg2;
        } else if (operation.equals("^")) {
            result = Math.pow(arg1, arg2);
        }
        return result;
    }
}
