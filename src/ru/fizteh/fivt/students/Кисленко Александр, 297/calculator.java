/**
 * Created with IntelliJ IDEA.
 * User: Александр
 * Date: 14.09.13
 * Time: 20:00
 * To change this template use File | Settings | File Templates.
 */
import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.lang.*;

public class calculator {
    private static final int RADIX = 17;

    public static void main(String[] args) {
        String inputString = "";
        for (String arg : args) {
            inputString = inputString + arg;
        }
        Scanner scan = new Scanner(inputString);
        PrintStream ps = new PrintStream(System.out);
        Stack<String> stack = new Stack();
        String s = "";
        scan.useRadix(RADIX);
        boolean minus = false;
        boolean change_sign = false;

        while (scan.hasNext()) {
            if(scan.hasNext("[0-9A-G][0-9A-G\\s\\(\\)\\+\\-\\*\\/]*")) {
                scan.useDelimiter("[\\s\\(\\)\\+\\-\\*\\/]");
                BigInteger buf = scan.nextBigInteger();
                int value = buf.intValue();
                if (change_sign) {
                    value = -value;
                    change_sign = false;
                }
                s = s + value + " ";
                minus = true;
                scan.useDelimiter("\\s");
            } else if(scan.hasNext("\\([0-9A-G\\s\\(\\)\\+\\-\\*\\/]*")) {
                minus = false;
                scan.useDelimiter("[0-9A-G\\s\\)\\+\\-\\*\\/]");
                String brackets = scan.next("\\(*");
                for (int i=0; i<brackets.length(); ++i) {
                    stack.push("(");
                }
                scan.useDelimiter("\\s");
            } else if(scan.hasNext("\\)[0-9A-G\\s\\(\\)\\+\\-\\*\\/]*")) {
                minus = true;
                scan.useDelimiter("[0-9A-G\\s\\(\\+\\-\\*\\/]");
                String brackets = scan.next("\\)*");
                for (int i=0; i<brackets.length(); ++i) {
                    while (!stack.isEmpty() && !stack.peek().equals("(")) {
                        s = s + stack.pop() + " ";
                    }
                    if (stack.isEmpty()) {
                        ps.println("Error: Expression not complied with the bracket balance. Terminate.");
                        System.exit(3);
                    }
                    stack.pop();
                }
                scan.useDelimiter("\\s");
            } else if(scan.hasNext("\\+[0-9A-G\\s\\(\\)\\+\\-\\*\\/]*")) {
                scan.useDelimiter("[0-9A-G\\s\\(\\)\\-\\*\\/]");
                String buf = scan.next("\\+*");
                if (buf.length()>1)
                {
                    ps.println("Error: Two or more pluses in succession. Terminate.");
                    System.exit(1);
                }
                minus = false;
                while(!stack.isEmpty() && !stack.peek().equals("(")) {
                    s = s + stack.pop() + " ";
                }
                stack.push("+");
                scan.useDelimiter("\\s");
            } else if(scan.hasNext("\\-[0-9A-G\\s\\(\\)\\+\\-\\*\\/]*")) {
                scan.useDelimiter("[0-9A-G\\s\\(\\)\\+\\*\\/]");
                String minuses = scan.next("\\-*");
                if (minuses.length()>1 || !minus) {
                    if (minus) {
                        if (minuses.length() % 2 == 0)  {
                            change_sign = !change_sign;
                        }
                        stack.push("-");
                    } else {
                        if (minuses.length() % 2 == 1) {
                            change_sign = !change_sign;
                        }
                    }
                    minus = false;
                }
                if(minus) {
                    while(!stack.isEmpty() && !stack.peek().equals("(")) {
                        s = s + stack.pop() + " ";
                    }
                    stack.push("-");
                }
                scan.useDelimiter("\\s");
            } else if(scan.hasNext("\\*[0-9A-G\\s\\(\\)\\+\\-\\*\\/]*")) {
                scan.useDelimiter("[0-9A-G\\s\\(\\)\\+\\-\\/]");
                String buf = scan.next("\\**");
                if (buf.length()>1)
                {
                    ps.println("Error: Two or more multiples in succession. Terminate.");
                    System.exit(1);
                }
                minus = false;
                while (!stack.isEmpty() && (stack.peek().equals("*") || stack.peek().equals("/"))) {
                    s = s + stack.pop() + " ";
                }
                stack.push("*");
                scan.useDelimiter("\\s");
            } else if(scan.hasNext("\\/[0-9A-G\\s\\(\\)\\+\\-\\*\\/]*")) {
                scan.useDelimiter("[0-9A-G\\s\\(\\)\\+\\-\\*]");
                String buf = scan.next("\\/*");
                if (buf.length()>1)
                {
                    ps.println("Error: Two or more divides in succession. Terminate.");
                    System.exit(1);
                }
                minus = false;
                while (!stack.isEmpty() && (stack.peek().equals("*") || stack.peek().equals("/"))) {
                    s = s + stack.pop() + " ";
                }
                stack.push("/");
                scan.useDelimiter("\\s");
            } else {
                ps.println("Error: Some bad symbol in input text. Terminate");
                System.exit(2);
            }
        }
        while (!stack.isEmpty())
        {
            if(stack.peek().equals("(")) {
                ps.println("Error: Expression not complied with the bracket balance. Terminate.");
                System.exit(3);
            }
            s = s + stack.pop() + " ";
        }

        String[] symbols = s.split(" ");
        Stack<Integer> operand_stack = new Stack();
        int oper1, oper2;

        for (String symbol : symbols) {
            if (symbol.equals("+")) {
                if(operand_stack.size()<2) {
                    ps.println("Error: Too many operations in this expression. Terminate.");
                    System.exit(4);
                }
                oper2 = operand_stack.pop();
                oper1 = operand_stack.pop();
                operand_stack.push(oper1 + oper2);
            } else if (symbol.equals("-")) {
                if(operand_stack.size()<2) {
                    ps.println("Error: Too many operations in this expression. Terminate.");
                    System.exit(4);
                }
                oper2 = operand_stack.pop();
                oper1 = operand_stack.pop();
                operand_stack.push(oper1 - oper2);
            } else if (symbol.equals("*")) {
                if(operand_stack.size()<2) {
                    ps.println("Error: Too many operations in this expression. Terminate.");
                    System.exit(4);
                }
                oper2 = operand_stack.pop();
                oper1 = operand_stack.pop();
                operand_stack.push(oper1 * oper2);
            } else if (symbol.equals("/")) {
                if(operand_stack.size()<2) {
                    ps.println("Error: Too many operations in this expression. Terminate.");
                    System.exit(4);
                }
                oper2 = operand_stack.pop();
                oper1 = operand_stack.pop();
                if (oper2 == 0) {
                    ps.print("Error: Dividing by zero. Terminate.");
                    System.exit(5);
                }
                operand_stack.push(oper1 / oper2);
            } else {
                operand_stack.push(Integer.parseInt(symbol));
            }
        }

        int answer=operand_stack.peek();
        ps.print(answer);
        System.exit(0);
    }
}