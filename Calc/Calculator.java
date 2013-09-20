import java.util.LinkedList;

public class Calculator {
    public static boolean IsOperation(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }
    public static int Priority(char operation) {
        switch(operation) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            default:
                return -1;
        }
    }
    public static void ProcessOperator(LinkedList<Integer> st, char operation) {
        int right = st.removeLast();
        int left = st.removeLast();
        switch (operation) {
            case '+':
                st.add(left + right);
                break;
            case '-':
                st.add(left - right);
                break;
            case '*':
                st.add(left * right);
                break;
            case '/':
                st.add(left / right);
                break;
        }
    }
    public static int Count(String s) {
        LinkedList<Integer> st = new LinkedList<Integer>();
        LinkedList<Character> op = new LinkedList<Character>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '(')
                op.add('(');
            else if (c == ')') {
                while (op.getLast() != '(')
                    ProcessOperator(st, op.removeLast());
                op.removeLast();
            }
            else if (IsOperation(c)) {
                while (!op.isEmpty() && Priority(op.getLast()) >= Priority(c))
                    ProcessOperator(st, op.removeLast());
                op.add(c);
            }
            else {
                StringBuilder sb = new StringBuilder();
                while (i < s.length() && IsDigit(s.charAt(i)))
                    sb.append(s.charAt(i++));
                --i;
                if (sb.length() == 0) {
                    throw new IllegalArgumentException("Unknown character");
                }
                st.add(Integer.parseInt(sb.toString(), 19));
            }
        }
        while (!op.isEmpty())
            ProcessOperator(st, op.removeLast());
        return st.get(0);
    }
    private static boolean IsDigit(char digit) {
        return Character.toString(digit).matches("[0-9a-iA-I]");
    }
    public void MyCalculator(double a, String operation, double b) {
        if (operation.equals("+")) {
            System.out.println (a + "+" + b + "=" + add(a,b));
            return ;
        }
        if (operation.equals("-")) {
            System.out.println (a + "-" + b + "=" + sub(a,b));
            return;
        }
        if (operation.equals("*")) {
            System.out.println (a + "*" + b + "=" + mul(a,b));
            return;
        }
        if (operation.equals("/")) {
            System.out.println (a + "/" + b + "=" + div(a,b));
            return;
        }
        System.err.print("Unknown operation");
        return;
    }
    public double add (double a, double b) {
        return a + b;
    }
    public double sub (double a, double b) {
        return a - b;
    }
    public double mul (double a, double b) {
        return a * b;
    }
    public double div (double a, double b) {
        return a / b;
    }
    public static void main (String[] args) {
        StringBuilder ExpressionBuilder = new StringBuilder();
        for (int i = 0; i < args.length; i++)
            ExpressionBuilder.append(args[i]);
        String Expression = ExpressionBuilder.toString().replace(" ", "");
        try {
            int result = Count(Expression);
            System.out.print(Integer.toString(result, 19).toUpperCase());
        }
        catch(IllegalArgumentException e) {
            System.err.print("The argument(s) contains unknown symbols");
        }
    }
}