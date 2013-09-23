package ru.fizteh.fivt.students.vorotilov.calculator;

/**
 * Created with IntelliJ IDEA.
 * User: justas
 * Date: 21.09.13
 * Time: 23:16
 * To change this template use File | Settings | File Templates.
 */

import java.util.Stack;

public class CalculatorMain {


    static boolean isOperator(char c) {
        return (c == '+') || (c == '-') || (c == '*') || (c == '/');
    }

    static int operatorPriority(char op) {
        if(op == '+' || op == '-') {
            return 1;
        } else if (op == '*' || op == '/') {
            return 2;
        } else {
            return -1;
        }
    }

    static void calculationStep(Stack<Integer> st, char op) throws Exception {
        if (st.size() < 2) {
            throw new Exception("Wrong number of arguments");
        }
        Integer r = st.pop();
        Integer l = st.pop();
        switch (op) {
            case '+': st.push(l + r);
                break;
            case '-': st.push(l - r);
                break;
            case '*': st.push(l * r);
                break;
            case '/': st.push(l / r);
                break;
        }

    }

    static String calculate(String inputString) throws Exception {
        Stack<Integer> st = new Stack();
        Stack<Character> op = new Stack();
        for (int i = 0; i < inputString.length(); ++i) {
            if ( inputString.charAt(i) != ' ' ) {
                if (inputString.charAt(i) == '(') {
                    op.push ('(');
                } else if (inputString.charAt(i) == ')') {
                    while (!op.empty() && op.peek() != '(') {
                        calculationStep(st, op.pop());
                    }
                    op.pop();
                } else if (isOperator(inputString.charAt(i))) {
                    while (!op.empty() && operatorPriority(op.peek()) >= operatorPriority(inputString.charAt(i))) {
                        calculationStep(st, op.pop());
                    }
                    op.push(inputString.charAt(i));
                }
                else {
                    StringBuilder operand = new StringBuilder();
                    while (i < inputString.length() && NumberWithBase.isFromAlphabet(inputString.charAt(i)) ) {
                        operand.append(inputString.charAt(i));
                        ++i;
                    }
                    --i;
                    st.push( NumberWithBase.numberToInt(operand.toString()) );
                }
            }
        }
        while (!op.empty()) {
            calculationStep(st, op.pop());
        }
        if (st.size() != 1) {
            throw new Exception("Wrong expression");
        }
        return NumberWithBase.intToNumber(st.peek());
    }

    public static void main(String[] args) {
        try {
            StringBuilder theWholeArgument = new StringBuilder();
            for (String arg: args) {
                theWholeArgument.append(arg);
                theWholeArgument.append(' ');
            }
            System.out.println(calculate(theWholeArgument.toString())  );
        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }
    }

}

class NumberWithBase {
    static final String NUMBER_SYMBOLS= "0123456789ABCDEFG";
    static final int NUMBER_BASE = NUMBER_SYMBOLS.length();

    static int numberToInt(String number) throws Exception {
        int result = 0;
        int start_position = 0;
        if (number.charAt(0) == '-') {
            start_position = 1;

        }
        for (int i = 0; i < number.length() - start_position; ++i) {
            int j = 0;
            while ((j < NUMBER_BASE) && (number.charAt(number.length() - i - 1) != NUMBER_SYMBOLS.charAt(j))) {
                ++j;
            }
            if (j >= NUMBER_BASE) {
                throw new Exception("Wrong symbol");
            }
            result += j * Math.pow(NUMBER_BASE, i);
        }
        if(start_position != 0) {
            return -result;
        } else {
            return result;
        }
    }

    static String intToNumber(int number) {
        StringBuilder result = new StringBuilder();
        boolean negative = (number < 0);
        if (negative) {
            number = -number;
        }
        if (number == 0) {
            return NUMBER_SYMBOLS.substring(0, 1);
        }
        while (number != 0) {
            int temp = number % NUMBER_BASE;
            result.append(NUMBER_SYMBOLS.charAt(temp));
            number -= temp;
            number /= NUMBER_BASE;
        }
        if (negative) {
            result.append('-');
        }
        result.reverse();
        return result.toString();
    }

    static boolean isFromAlphabet(char c) {
        for (int i = 0; i < NUMBER_BASE; ++i) {
            if (c == NUMBER_SYMBOLS.charAt(i)) {
                return true;
            }
        }
        return false;
    }

    static void test(int a, int b) {
        try {
            int errors = 0;
            for (int i = a; i <= b; ++i) {
                if (NumberWithBase.numberToInt(NumberWithBase.intToNumber(i)) != i) {
                    System.out.println("At " + i + "conversion is wrong");
                    ++errors;
                }
            }
            System.out.println("Test is completed with " + errors + " errors");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}

