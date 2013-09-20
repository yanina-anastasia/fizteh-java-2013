/**
 Class Calculator
 Version 1.0
 @author Alexandra Belousova (sashbel@inbox.ru)
 */
package ru.fizteh.fivt.students.belousova.calculator;
import java.util.EmptyStackException;
import java.util.Stack;

public class Calculator {

    private static Stack<Integer> countStack = new Stack<Integer>();
    private static Stack<String> operatorStack = new Stack<String>();
    private static Stack<Integer> numberStack = new Stack<Integer>();
    private static boolean unarFlag = true;
    private static int[][] priority = {{4, 1, 1, 1, 5},
            {2, 2, 1, 1, 2},{2, 2, 2, 1, 2},{5, 1, 1, 1, 3}};

    public static void main(String[] args){
        String s = "";

        for (String si : args){
            s += si;
        }
        try {
            if (!s.equals("")) System.out.println(calculate(s));
        }
        catch (IllegalArgumentException e)
        {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    public static String calculate(String s) {
        try {
            operatorStack.push("&");
            s = s + '&';
            parse(s);
            return convertRadix(countStack.pop());
        }
        catch (EmptyStackException e) {
            throw new IllegalArgumentException("Некорректное выражение");
        }
    }

    private static void parse(String s) {
        char[] number = {' '};

        for (char c : s.toCharArray()) {
            if ((c == '-') && unarFlag) {
                unarFlag = false;
                parse ("0-");
                continue;
            }
            if ((c == '+') && unarFlag) {
                continue;
            }
            switch (c) {
                case ')':
                case '&':
                case '(':
                case '+':
                case '-':
                case '*':
                case '/':
                    if (!numberStack.empty()) {
                        countStack.push(calculateNumber());
                    }
                    unwindOpStack(c);
                    if ((c == '(') || (c == '&')) {
                        unarFlag = true;
                    }
                    else unarFlag = false;
                    break;
                case ' ': break;
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case '0':
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                    number[0] = c;
                    numberStack.push(Integer.parseInt(new String(number), 18));
                    unarFlag = false;
                    break;
                default:
                    throw new IllegalArgumentException("Недопустимые символы в выражении");

            }
        }
    }

    public static int operate(char op, Integer b, Integer a) {
        switch (op) {
            case '+':
                return  a+b;
            case '-':
                return  a-b;
            case '*':
                return  a*b;
            case '/':
                if (b.equals(0)) {
                    throw new IllegalArgumentException("Деление на ноль");
                }
                return  a/b;
        }
        return 0;
    }

    private static int calculateNumber() {
        int result = 0;
        int k = 1;

        if (!numberStack.empty()) {
            while (!numberStack.empty()) {
                result += numberStack.pop() * k;
                k *= 18;
            }

        }
        return result;
    }

    private static int convertOp2Int (char op) {
        switch (op) {
            case '&': return 0;
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            case '(':
                return 3;
            case ')':
                return 4;
            default:
                return -1;
        }
    }

    private static void unwindOpStack(char newOperation) {
        if (!operatorStack.empty()) {

            char[] newOp = {' '};
            char op = operatorStack.peek().charAt(0);
            int a = convertOp2Int(op);
            int b = convertOp2Int(newOperation);
            int action = priority[a][b];
            Integer opResult;

            switch (action) {
                case 1:
                    newOp[0] = newOperation;
                    operatorStack.push(new String(newOp));
                    break;
                case 2:
                    opResult = operate(op, countStack.pop(), countStack.pop());
                    countStack.push(opResult);
                    operatorStack.pop();
                    unwindOpStack(newOperation);
                    break;
                case 3:
                    operatorStack.pop();
                    break;
                case 4:
                    break;
                case 5:
                    throw new IllegalArgumentException("Нарушен баланс скобок");


            }
        }
    }

    private static String convertRadix (int a) {

        String result = "";
        int digit;
        boolean negative = (a < 0);
        a = Math.abs(a);

        if (a == 0) return "0";

        while (a != 0) {
            digit = a%18;
            a /= 18;
            switch (digit) {
                case 0:
                    result = '0' + result;
                    break;
                case 1:
                    result = '1' + result;
                    break;
                case 2:
                    result = '2' + result;
                    break;
                case 3:
                    result = '3' + result;
                    break;
                case 4:
                    result = '4' + result;
                    break;
                case 5:
                    result = '5' + result;
                    break;
                case 6:
                    result = '6' + result;
                    break;
                case 7:
                    result = '7' + result;
                    break;
                case 8:
                    result = '8' + result;
                    break;
                case 9:
                    result = '9' + result;
                    break;
                case 10:
                    result = 'A' + result;
                    break;
                case 11:
                    result = 'B' + result;
                    break;
                case 12:
                    result = 'C' + result;
                    break;
                case 13:
                    result = 'D' + result;
                    break;
                case 14:
                    result = 'E' + result;
                    break;
                case 15:
                    result = 'F' + result;
                    break;
                case 16:
                    result = 'G' + result;
                    break;
                case 17:
                    result = 'H' + result;
                    break;
            }
        }
        if (negative) result = '-' + result;
        return  result;
    }
}

