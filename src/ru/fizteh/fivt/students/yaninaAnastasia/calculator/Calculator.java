package ru.fizteh.fivt.students.yaninaAnastasia.calculator;

import java.math.BigInteger;
import java.util.LinkedList;


public class Calculator {
    public static boolean isOperation(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    public static int priority(char operation) {
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

    public static void processOperator(LinkedList<Integer> st, char operation) {
        int right = st.removeLast();
        int left = st.removeLast();
        if ((left > 0) && (right > 0) && ((Integer.MAX_VALUE - left <= right) || (right * left >= Integer.MAX_VALUE) || (left / right >= Integer.MAX_VALUE))) {
            System.err.println("Error: integer overflow");
            System.exit(1);
        }
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
                if (right == 0) {
                    System.err.println("Error: dividing by zero");
                    System.exit(1);
                }
                st.add(left / right);
                break;
        }
    }

    public static int count(String s) {
        LinkedList<Integer> st = new LinkedList<Integer>();
        LinkedList<Character> op = new LinkedList<Character>();
        int prevSym = -1;
        int bracketBalance = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isWhitespace(c)) {
                continue;
            }
            else if (c == '(') {
                op.add('(');
                prevSym = 3;
                bracketBalance++;
            }
            else if (c == ')') {
                bracketBalance--;
                while (op.getLast() != '(') {
                    processOperator(st, op.removeLast());
                }
                if (prevSym == 3) {
                    System.err.println("Error: empty brackets");
                    System.exit(1);
                }
                op.removeLast();
            }
            else if (isOperation(c)) {
                while (!op.isEmpty() && priority(op.getLast()) >= priority(c)) {
                    if (prevSym == 1) {
                        System.err.println("Error: too many operators without numbers");
                        System.exit(1);
                    }
                    processOperator(st, op.removeLast());
                }
                op.add(c);
                prevSym = 1;
            }
            else {
                StringBuilder sb = new StringBuilder();
                while (i < s.length() && isDigit(s.charAt(i))) {
                    if (prevSym == 2) {
                        System.err.println("Error: too many numbers without operators");
                        System.exit(1);
                    }
                    sb.append(s.charAt(i++));
                }
                --i;
                if (sb.length() == 0) {
                    System.err.println("Error: unknown symbol(s)");
                    System.exit(1);
                }
                if (BigInteger.valueOf(Long.parseLong(sb.toString())).compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) == 1) {
                    System.err.println("Error: integer overflow");
                    System.exit(1);
                }
                st.add(Integer.parseInt(sb.toString(), 19));
                prevSym = 2;
            }
        }
        while (!op.isEmpty()) {
            if (bracketBalance != 0) {
                System.err.println("Error: wrong bracket balance");
                System.exit(1);
            }
            processOperator(st, op.removeLast());
        }

        return st.get(0);
    }

    private static boolean isDigit(char digit) {
        return Character.toString(digit).matches("[0-9a-iA-I]");
    }

    public static void main (String[] args) {
        StringBuilder ExpressionBuilder = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            ExpressionBuilder.append(args[i]);
            ExpressionBuilder.append(" ");
        }
        String Expression = ExpressionBuilder.toString();
        int result = count(Expression);
        System.out.print(Integer.toString(result, 19).toUpperCase());
    }
}