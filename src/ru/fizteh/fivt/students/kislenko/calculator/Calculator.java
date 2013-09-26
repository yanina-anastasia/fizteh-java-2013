package ru.fizteh.fivt.students.kislenko.calculator;
/**
 * Created with IntelliJ IDEA.
 * User: Александр
 * Date: 14.09.13
 * Time: 20:00
 * To change this template use File | Settings | File Templates.
 */

import java.io.IOException;
import java.io.PrintStream;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Stack;

public class Calculator {
    private static final int RADIX = 17;

    public static void main(String[] args) throws IOException, InputMismatchException {
        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            sb.append(arg);
        }
        String inputString = sb.toString();
        PrintStream ps = new PrintStream(System.out);
        if (inputString.length() == 0) {
            throw new IOException("Empty input.");
        }
        Scanner scan = new Scanner(inputString);
        Stack<String> stack = new Stack<String>();
        StringBuilder polandBuilder = new StringBuilder();
        String s = "";
        scan.useRadix(RADIX);
        boolean minus = false;
        boolean changeSign = false;

        while (scan.hasNext()) {
            if (scan.hasNext("[0-9A-G][0-9A-G\\s\\(\\)\\+\\-\\*\\/]*")) {
                scan.useDelimiter("[\\s\\(\\)\\+\\-\\*\\/]");
                int value;
                try {
                    value = scan.nextInt();
                } catch (InputMismatchException e) {
                    throw new IOException("Too big value in input.");
                }
                if (changeSign) {
                    value = -value;
                    changeSign = false;
                }
                polandBuilder.append(value).append(" ");
                minus = true;
                scan.useDelimiter("\\s");
            } else if (scan.hasNext("\\([0-9A-G\\s\\(\\)\\+\\-\\*\\/]*")) {
                minus = false;
                scan.useDelimiter("[0-9A-G\\s\\)\\+\\-\\*\\/]");
                String brackets = scan.next("\\(*");
                for (int i = 0; i < brackets.length(); ++i) {
                    stack.push("(");
                }
                scan.useDelimiter("\\s");
            } else if (scan.hasNext("\\)[0-9A-G\\s\\(\\)\\+\\-\\*\\/]*")) {
                minus = true;
                scan.useDelimiter("[0-9A-G\\s\\(\\+\\-\\*\\/]");
                String brackets = scan.next("\\)*");
                for (int i = 0; i < brackets.length(); ++i) {
                    while (!stack.isEmpty() && !stack.peek().equals("(")) {
                        polandBuilder.append(stack.pop()).append(" ");
                    }
                    if (stack.isEmpty()) {
                        throw new IOException("Expression not complied with the bracket balance.");
                    }
                    stack.pop();
                }
                scan.useDelimiter("\\s");
            } else if (scan.hasNext("\\+[0-9A-G\\s\\(\\)\\+\\-\\*\\/]*")) {
                scan.useDelimiter("[0-9A-G\\s\\(\\)\\-\\*\\/]");
                String buf = scan.next("\\+*");
                if (buf.length() > 1) {
                    throw new IOException("Two or more pluses in succession.");
                }
                minus = false;
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    polandBuilder.append(stack.pop()).append(" ");
                }
                stack.push("+");
                scan.useDelimiter("\\s");
            } else if (scan.hasNext("\\-[0-9A-G\\s\\(\\)\\+\\-\\*\\/]*")) {
                scan.useDelimiter("[0-9A-G\\s\\(\\)\\+\\*\\/]");
                String minuses = scan.next("\\-*");
                if (minuses.length() > 1 || !minus) {
                    if (minus) {
                        if (minuses.length() % 2 == 0) {
                            changeSign = !changeSign;
                        }
                        stack.push("-");
                    } else {
                        if (minuses.length() % 2 == 1) {
                            changeSign = !changeSign;
                        }
                    }
                    minus = false;
                }
                if (minus) {
                    while (!stack.isEmpty() && !stack.peek().equals("(")) {
                        polandBuilder.append(stack.pop()).append(" ");
                    }
                    stack.push("-");
                }
                scan.useDelimiter("\\s");
            } else if (scan.hasNext("\\*[0-9A-G\\s\\(\\)\\+\\-\\*\\/]*")) {
                scan.useDelimiter("[0-9A-G\\s\\(\\)\\+\\-\\/]");
                String buf = scan.next("\\**");
                if (buf.length() > 1) {
                    throw new IOException("Two or more multiples in succession.");
                }
                minus = false;
                while (!stack.isEmpty() && (stack.peek().equals("*") || stack.peek().equals("/"))) {
                    polandBuilder.append(stack.pop()).append(" ");
                }
                stack.push("*");
                scan.useDelimiter("\\s");
            } else if (scan.hasNext("\\/[0-9A-G\\s\\(\\)\\+\\-\\*\\/]*")) {
                scan.useDelimiter("[0-9A-G\\s\\(\\)\\+\\-\\*]");
                String buf = scan.next("\\/*");
                if (buf.length() > 1) {
                    throw new IOException("Two or more divides in succession.");
                }
                minus = false;
                while (!stack.isEmpty() && (stack.peek().equals("*") || stack.peek().equals("/"))) {
                    polandBuilder.append(stack.pop()).append(" ");
                }
                stack.push("/");
                scan.useDelimiter("\\s");
            } else {
                throw new IOException("Some bad symbol in input text.");
            }
        }
        while (!stack.isEmpty()) {
            if (stack.peek().equals("(")) {
                throw new IOException("Expression not complied with the bracket balance.");
            }
            polandBuilder.append(stack.pop()).append(" ");
        }
        s = polandBuilder.toString();
        if (s.equals("")) {
            throw new IOException("Empty input.");
        }

        String[] symbols = s.split(" ");
        Stack<Integer> operandStack = new Stack<Integer>();
        int oper1, oper2;

        for (String symbol : symbols) {
            if (symbol.equals("+")) {
                if (operandStack.size() < 2) {
                    throw new IOException("Too many operations in this expression.");
                }
                oper2 = operandStack.pop();
                oper1 = operandStack.pop();
                if (Integer.MAX_VALUE - oper1 < oper2) {
                    throw new ArithmeticException("Too big values in expression.");
                }
                operandStack.push(oper1 + oper2);
            } else if (symbol.equals("-")) {
                if (operandStack.size() < 2) {
                    throw new IOException("Too many operations in this expression.");
                }
                oper2 = operandStack.pop();
                oper1 = operandStack.pop();
                operandStack.push(oper1 - oper2);
            } else if (symbol.equals("*")) {
                if (operandStack.size() < 2) {
                    throw new IOException("Too many operations in this expression.");
                }
                oper2 = operandStack.pop();
                oper1 = operandStack.pop();
                operandStack.push(oper1 * oper2);
                if (Integer.MAX_VALUE / oper2 < oper1) {
                    throw new ArithmeticException("Too big values in expression.");
                }
            } else if (symbol.equals("/")) {
                if (operandStack.size() < 2) {
                    throw new IOException("Too many operations in this expression.");
                }
                oper2 = operandStack.pop();
                oper1 = operandStack.pop();
                if (oper2 == 0) {
                    throw new RuntimeException("Dividing by zero.");
                }
                operandStack.push(oper1 / oper2);
            } else {
                operandStack.push(Integer.parseInt(symbol));
            }
        }

        int answer = operandStack.pop();
        if (!operandStack.isEmpty()) {
            throw new IOException("Too many operands.");
        }
        ps.print(answer);
        System.exit(0);
    }
}