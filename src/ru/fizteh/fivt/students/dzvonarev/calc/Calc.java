package ru.fizteh.fivt.students.dzvonarev.calc;

import java.util.Stack;
import java.math.BigInteger;

class Calc {

    public static int priority(String str) {
        if (str.equals("+") || str.equals("-")) {
            return 1;
        }
        if (str.equals("*") || str.equals("/")) {
            return 2;
        }
        if (str.equals("(")) {
            return 0;
        }
        if (!str.equals("*") && !str.equals("-") && !str.equals("/")
                && !str.equals("+") && !str.equals("(")) {
            System.err.println("Wrong operation");
            System.exit(1);
        }
        return 0;
    }
    
    public static BigInteger doOp(BigInteger num1, String op, BigInteger num2) {
        if (op.equals("+")) {
            return num1.add(num2);
        }
        if (op.equals("*")) {
            return num1.multiply(num2);
        }
        if (op.equals("-")) {
            return num1.subtract(num2);
        }
        if (op.equals("/") && !num2.equals(0)) {
            return num1.divide(num2);
        } else {
            System.err.println("Dividing by zero");
            System.exit(1);
        }
        return BigInteger.valueOf(0);
    }

    private static Stack<String> number = new Stack();
    private static Stack<String> operation = new Stack();

    public static void currentOperation() {
        BigInteger num1 = new BigInteger(number.pop(), 19);
        BigInteger num2 = new BigInteger(number.pop(), 19);
        String op = operation.pop();
        BigInteger num = doOp(num2, op, num1);
        String newStr = num.toString(19);
        number.push(newStr);
    }

    public static void main(String[] args) {
        for (int i = 0; i < args.length; ++i) {
            int j = 0;
            while (j < args[i].length()) {
                String str = "";
                boolean num = true;
                while (num && j < args[i].length()) {
                    String strNew = str + args[i].charAt(j);
                    if (strNew.matches("[0-9A-Ia-i]+")) {
                        str = strNew;
                        num = true;
                        ++j;
                    } else {
                        num = false;
                    }
                }
                str = str.substring(0, str.length()); /*
                                                       * deleting last - not
                                                       * number symbol
                                                       */
                if (!str.isEmpty()) { /* we've found number */
                    number.push(str);
                    continue;
                }
                str = "" + args[i].charAt(j);
                if (str.equals(" ")) {
                    ++j;
                    continue;
                }

                if (!str.equals("+") && !str.equals("-") && !str.equals("*")
                        && !str.equals("/") && !str.equals("(")
                        && !str.equals(")")) {
                    System.err.println("Unknown symbol =  " + str);
                    System.exit(1);
                }

                if (str.equals("+") || str.equals("-") || str.equals("*")
                        || str.equals("/")) { /* we've found operation */
                    if (operation.isEmpty()) {
                        operation.push(str);
                    } else {
                        if (priority(operation.peek()) < priority(str)) {
                            operation.push(str);
                        } else { /* now we do if op's bigger priority then now */                            
                            while (priority(operation.peek()) >= priority(str)) {
                                if (number.size() < 2) {
                                    System.err.print("I can't do " + str + " operation");
                                    System.exit(1);
                                }
                                currentOperation();
                                if (operation.isEmpty()) {
                                    break;
                                }
                            }
                            operation.push(str);
                        }
                    }
                    ++j;
                    continue;
                }

                if (str.equals("(")) {
                    operation.push(str);
                    ++j;
                    continue;
                }

                if (str.equals(")")) {
                    if (!operation.isEmpty()) {
                        while (!operation.peek().equals("(")) {
                            currentOperation();
                            if (operation.isEmpty()) {
                                System.err.println("Wrong bracket sequence");
                                System.exit(1);
                            }
                        }
                        operation.pop(); // we pop open clause
                    } else {
                        System.err.println("Wrong bracket sequence");
                        System.exit(1);
                    }
                    ++j;
                    continue;
                }
            }
        }

        while (!operation.isEmpty()) {
            if (number.size() < 2) {
                System.err.print("I can't do " + operation.peek() + " operation");
                System.exit(1);
            }
            if (operation.peek().equals("(")) {
                System.err.print("Wrong bracket sequence");
                System.exit(1);
            }
            currentOperation();
        }
        if (number.size() != 1) {
            System.err.println("Wrong expression");
            System.exit(1);
        }
        System.out.println(number.pop());
    }
}
