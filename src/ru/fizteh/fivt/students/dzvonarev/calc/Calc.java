package ru.fizteh.fivt.students.dzvonarev.calc;

import java.util.Stack;

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

    public static boolean noOverFlowAdd(long num1, long num2) {
        if (num2 <= 0) {
            if (Long.MIN_VALUE - num2 <= num1) {
                return true;
            } else {
                return false;
            }
        }
        if (num2 > 0) {
            if (Long.MAX_VALUE - num2 >= num1) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public static boolean noOverFlowMinus(long num1, long num2) {
        if (num2 <= 0) {
            if (Long.MAX_VALUE + num2 >= num1) {
                return true;
            } else {
                return false;
            }
        }
        if (num2 > 0) {
            if (Long.MIN_VALUE + num2 <= num1) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public static boolean noOverFlowMul(long num1, long num2) {
        if (num1 == 0 || num2 == 0) {
            return true;
        }
        if (num1 > 0 && num2 > 0 || num1 < 0 && num2 < 0 || num1 < 0) {
            if ((Long.MIN_VALUE / num2 <= num1)
                    && (num1 <= Long.MAX_VALUE / num2)) {
                return true;
            } else {
                return false;
            }
        }
        if (num2 < 0) {
            if ((Long.MIN_VALUE / num2 >= num1)
                    && (num1 >= Long.MAX_VALUE / num2)) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public static long doOp(long num1, String op, long num2) {
        if (op.equals("+")) {
            if (noOverFlowAdd(num1, num2)) {
                return num1 + num2;
            } else {
                System.err.println("Too big numbers plus");
                System.exit(1);
            }
        }
        if (op.equals("*")) {
            if (noOverFlowMul(num1, num2)) {
                return num1 * num2;
            } else {
                System.err.println("Too big numbers mul");
                System.exit(1);
            }
        }
        if (op.equals("-")) {
            if (noOverFlowMinus(num1, num2)) {
                return num1 - num2;
            } else {
                System.err.println("Too big numbers min");
                System.exit(1);
            }
        }
        if (op.equals("/") && num2 != 0) {
            return num1 / num2;
        } else {
            System.err.println("Dividing by zero");
            System.exit(1);
        }
        return 0;
    }

    private static Stack<String> number = new Stack();
    private static Stack<String> operation = new Stack();

    public static void currentOperation() {
        long num1 = Integer.parseInt(number.pop(), 19);
        long num2 = Integer.parseInt(number.pop(), 19);
        String op = operation.pop();
        long num = doOp(num2, op, num1);
        String newStr = Long.toString(num, 19);
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
                        && !str.equals("/") && !str.equals("(") && !str.equals(")")) {
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
