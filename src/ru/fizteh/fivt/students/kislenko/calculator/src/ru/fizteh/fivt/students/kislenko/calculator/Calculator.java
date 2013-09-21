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
import java.math.BigInteger;
import java.util.Scanner;
import java.util.Stack;

public class Calculator {
    private static final int RADIX = 17;

    public static void main(String[] args) throws IOException {
        String inputString = "";
        for (String arg : args) {
            inputString = inputString + arg;
        }
        PrintStream ps = new PrintStream(System.out);
        if (inputString.length() == 1) {
            ps.print(0);
            System.exit(0);
        }
        Scanner scan = new Scanner(inputString);
        Stack<String> stack = new Stack();
        String s = "";
        scan.useRadix(RADIX);
        boolean minus = false;
        boolean changeSign = false;

        while (scan.hasNext()) {
            if (scan.hasNext("[0-9A-G][0-9A-G\\s\\(\\)\\+\\-\\*\\/]*")) {
                scan.useDelimiter("[\\s\\(\\)\\+\\-\\*\\/]");
                BigInteger buf = scan.nextBigInteger();
                int value = buf.intValue();
                if (changeSign) {
                    value = -value;
                    changeSign = false;
                }
                s = s + value + " ";
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
                        s = s + stack.pop() + " ";
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
                    s = s + stack.pop() + " ";
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
                        s = s + stack.pop() + " ";
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
                    s = s + stack.pop() + " ";
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
                    s = s + stack.pop() + " ";
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
            s = s + stack.pop() + " ";
        }

        String[] symbols = s.split(" ");
        Stack<Integer> operandStack = new Stack();
        int oper1, oper2;

        for (String symbol : symbols) {
            if (symbol.equals("+")) {
                if (operandStack.size() < 2) {
                    throw new IOException("Too many operations in this expression.");
                }
                oper2 = operandStack.pop();
                oper1 = operandStack.pop();
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
            } else if (symbol.equals("/")) {
                if (operandStack.size() < 2) {
                    throw new IOException("Too many operations in this expression.");
                }
                oper2 = operandStack.pop();
                oper1 = operandStack.pop();
                if (oper2 == 0) {
                    throw new IOException("Dividing by zero.");
                }
                operandStack.push(oper1 / oper2);
            } else {
                operandStack.push(Integer.parseInt(symbol));
            }
        }

        int answer = operandStack.peek();
        ps.print(answer);
        System.exit(0);
    }
}
