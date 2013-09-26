package ru.fizteh.fivt.students.annasavinova.calculator;

import java.util.Scanner;
import java.util.Stack;

public class Calculator {
    static Stack<String> signStack = new Stack<>();
    static Stack<Integer> digitStack = new Stack<>();
    
    public static int priority(String c) {
        if (c.equals("+") || c.equals("-")) {
            return 0;
        } else if (c.equals("*") || c.equals("/")) {
                return 1;
            } else {
                return -1;
            }
    }
    
    public static int getVal() {
        String sign = signStack.pop();
        int secondVal = digitStack.pop();
        int firstVal = digitStack.pop();
        int res = 0;
        long tmp = 0;
        switch (sign) {
        case "+":
            tmp = firstVal + secondVal;
            if (tmp * Long.signum(tmp) > Integer.MAX_VALUE) {
                System.err.println("IntOverflow");
                System.exit(1);
            } else {
                res = (int) tmp;
            }
            break;
        case "-":
            tmp = firstVal - secondVal;
            if (tmp * Long.signum(tmp) > Integer.MAX_VALUE) {
                System.err.println("IntOverflow");
                System.exit(1);
            } else {
                res = (int) tmp;
            }
            break;
        case "*":
            tmp = firstVal * secondVal;
            if (tmp * Long.signum(tmp) > Integer.MAX_VALUE) {
                System.err.println("IntOverflow");
                System.exit(1);
            } else {
                res = (int) tmp;
            }
            break;
        case "/":
            if (secondVal != 0) {
                res =  firstVal / secondVal;
            } else {
                System.err.println("Dividing by zero");
                System.exit(2);
            }
            break;
        }
        return res;
    }
    
    public static boolean checkBrackets(String input) {
        int count = 0;
        for (int i = 0; i < input.length(); ++i) {
            if (input.charAt(i) == '(') {
                ++count;
            } else if (input.charAt(i) == ')') {
                --count;
            }
            if (count < 0) {
                return false;
            }
        }
        if (count != 0) {
            return false;
        }
        return true;
    }
    
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please, input expression, that you want to know");
            System.out.println("Acceptable symbols: +, -, *, /");
            System.out.println("Radix: 18");
            return;
        }
        StringBuffer s = new StringBuffer();
        s.append("(");
        for (int i = 0; i < args.length; ++i) {
            s.append(args[i]);
        }
        s.append(")");
        String str = s.toString();
        str = str.replace(" ", "");
        if (!checkBrackets(str)) {
            System.err.println("Incorrect bracket sequance");
            System.exit(3);
        }
        str = str.replace("(", "( ");
        str = str.replace(")", " )");
        str = str.replace("+", " + ");
        str = str.replace("-", " - ");
        str = str.replace("*", " * ");
        str = str.replace("/", " / ");
        Scanner sc = new Scanner(str);
        sc.useRadix(18);
        sc.useDelimiter(" ");
        while (sc.hasNext()) {
            if (sc.hasNext("[/(/)/+-/*//]")) {
                String currentSymbol = sc.next();
                switch (currentSymbol) {
                case "(": 
                    signStack.push(currentSymbol);
                    break;
                case ")":
                    while (!(signStack.peek()).equals("(")) {
                            digitStack.push(getVal());
                    }
                    signStack.pop();
                    break;
                default:
                    if (signStack.empty()
                            || priority(signStack.peek()) < priority(currentSymbol)) {
                        signStack.push(currentSymbol);
                    } else {
                        while (priority(signStack.peek()) >= priority(currentSymbol)) {
                                digitStack.push(getVal());
                        }
                        signStack.push(currentSymbol);
                    }
                }
            } else {
                if (sc.hasNextInt()) {
                    int currentDigit = sc.nextInt();
                    digitStack.push(currentDigit);
                } else {
                    System.err.println("Incorrect expression: incorrect symbol or num of operators and operands");
                    sc.close();
                    System.exit(4);
                }
            }
        }
        int result = digitStack.pop();
        if (digitStack.empty()) {
            System.out.println("Result: " + Integer.toString(result, 18));
        } else {
            System.err.println("Incorrect num of operators and operands" + digitStack.peek());
            sc.close();
            System.exit(5);
        }
        sc.close();
    }
}