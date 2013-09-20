package ru.fizteh.fivt.students.anastasyev.calculator;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.Stack;
import java.lang.String;

public class Main {
    public static final int RADIX = 17;

    public static String ToRPN(String[] args) {
        String simbols = new String();
        Stack<String> simbols_stack = new Stack();
        boolean minus = true;
        boolean sign = false;
        for (int i = 0; i < args.length; ++i) {
            for (int j = 0; j < args[i].length(); ++j) {
                if (args[i].charAt(j) >= '0' && args[i].charAt(j) <= '9'
                        || args[i].charAt(j) >= 'A' && args[i].charAt(j) <= 'G') {
                    int k = j + 1;
                    while (k < args[i].length() && (args[i].charAt(k) >= '0' && args[i].charAt(k) <= '9'
                            || args[i].charAt(k) >= 'A' && args[i].charAt(k) <= 'G')) {
                        ++k;
                    }
                    int number = Integer.parseInt(args[i].substring(j, k), RADIX);
                    if (sign) {
                        number = -number;
                        sign = false;
                    }
                    simbols += number + " ";
                    minus = false;
                    j += k - j - 1;
                } else if (args[i].charAt(j) == '(') { // (
                    int k = j + 1;
                    int count = 1;
                    while (k < args[i].length() && (args[i].charAt(k) == '(' || args[i].charAt(k) == ' ')) {
                        if (args[i].charAt(j) == '(')
                            ++count;
                        ++k;
                    }
                    for (int p = 0; p < count; ++p) {
                        simbols_stack.push("(");
                    }
                    minus = true;
                    j += count - 1;
                } else if (args[i].charAt(j) == ')') {
                    while (j < args[i].length() && (args[i].charAt(j) == ')' || args[i].charAt(j) == ' ')) {
                        if (args[i].charAt(j) == ' ') {
                            ++j;
                        } else {
                            while (!simbols_stack.empty() && !simbols_stack.peek().equals("(")) {
                                simbols += simbols_stack.pop() + " ";
                            }
                            if (simbols_stack.empty()) {
                                System.err.println("Incorrect bracket balance!");
                                System.exit(1);
                            }
                            simbols_stack.pop();
                            ++j;
                        }
                    }
                    minus = false;
                    --j;
                } else if (args[i].charAt(j) == '+') {
                    while (!simbols_stack.empty() && !simbols_stack.peek().equals("(")) {
                        simbols += simbols_stack.pop() + " ";
                    }
                    minus = false;
                    simbols_stack.push("+");
                } else if (args[i].charAt(j) == '-') {
                    if (minus) {
                        sign = true;
                    } else {
                        while (!simbols_stack.empty() && !simbols_stack.peek().equals("(")) {
                            simbols += simbols_stack.pop() + " ";
                        }
                        simbols_stack.push("-");
                    }
                } else if (args[i].charAt(j) == '*') {
                    while (!simbols_stack.empty() && (simbols_stack.peek().equals("*") || simbols_stack.peek().equals("/"))) {
                        simbols += simbols_stack.pop() + " ";
                    }
                    minus = false;
                    simbols_stack.push("*");
                } else if (args[i].charAt(j) == '/') {
                    while (!simbols_stack.empty() && (simbols_stack.peek().equals("*") || simbols_stack.peek().equals("/"))) {
                        simbols += simbols_stack.pop() + " ";
                    }
                    minus = false;
                    simbols_stack.push("/");
                } else if (args[i].charAt(j) == ' ') {
                } else {
                    System.err.println("Bad symbol: " + args[i].charAt(j));
                    System.exit(1);
                }
            }
        }
        while (!simbols_stack.isEmpty()) {
            if (simbols_stack.peek().equals("(")) {
                System.err.println("Incorrect bracket balance!");
                System.exit(1);
            }
            simbols += simbols_stack.pop() + " ";
        }
        return simbols;
    }

    public static Integer Calcs(String expression) {
        Stack<Integer> values = new Stack<Integer>();
        String[] symbols = expression.split(" ");
        int value1;
        int value2;

        for (String symbol : symbols) {
            if (symbol.equals("+")) {
                if (values.size() < 2) {
                    System.err.println("Too many operations");
                    System.exit(1);
                }
                value2 = values.pop();
                value1 = values.pop();
                values.push(value1 + value2);
            } else if (symbol.equals("-")) {
                if (values.size() < 2) {
                    System.err.println("Too many operations");
                    System.exit(1);
                }
                value2 = values.pop();
                value1 = values.pop();
                values.push(value1 - value2);
            } else if (symbol.equals("*")) {
                if (values.size() < 2) {
                    System.err.println("Too many operations");
                    System.exit(1);
                }
                value2 = values.pop();
                value1 = values.pop();
                values.push(value1 * value2);
            } else if (symbol.equals("/")) {
                if (values.size() < 2) {
                    System.err.println("Too many operations");
                    System.exit(1);
                }
                value2 = values.pop();
                value1 = values.pop();
                if (value2 == 0) {
                    System.err.println("Divide by zero");
                    System.exit(1);
                }
                values.push(value1 / value2);
            } else {
                values.push(Integer.parseInt(symbol));
            }
        }
        return values.peek();
    }


    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: numbers in 17-th system <[0..9, A..G]> \n operations: +, -, *, /\n brackets: (, )");
            System.exit(1);
        }
        String expression = new String();
        try {
            expression = ToRPN(args);
        } catch (NumberFormatException e) {
            System.err.println("Invalid number: " + e);
            System.exit(1);
        }
        try {
            Integer result = Calcs(expression);
            System.out.println(Integer.toString(result, RADIX));
            System.exit(0);
        } catch (ArithmeticException e) {
            System.err.println(e);
            System.exit(1);
        }
    }
}
