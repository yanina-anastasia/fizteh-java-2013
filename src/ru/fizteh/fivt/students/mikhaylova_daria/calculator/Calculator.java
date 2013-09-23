package ru.fizteh.fivt.students.mikhaylova_daria.calculator

import java.util.Stack;
import java.util.Vector;

public class Calculator {
	
   public static void main (String[] argc) {
            StringBuilder builderArgument = new StringBuilder();
            for (int i = 0; i < argc.length; ++i) {
                 builderArgument.append(argc[i]);
            }
            try {
                String answer = calculatorRPN(converterToRPN(builderArgument));
                System.out.println(answer);
            } catch (Exception exp){
                System.err.println(exp.getLocalizedMessage());
                return;
            }
       }


    private static Vector <String> converterToRPN (StringBuilder arg) throws Exception {
        Stack<String> stack = new Stack<String>(); 
        Vector<String> vectorRPN = new Vector<String>();
        Integer number = Integer.MAX_VALUE;
        int i = 0;
        int operatorOrNum = 0;
        int bracketBalance = 0;
        int signOfNumber = 1;
        boolean lastCharWasNumber = false;
        while (i < arg.length()) {
            if ('0' <= arg.charAt(i) && arg.charAt(i) <= '9') {
                if (number == Integer.MAX_VALUE) {
                    number = arg.charAt(i) - '0';
                } else {
                    number = 17 * number + (arg.charAt(i) - 48);
                }
            } else {
                if ('A' <= arg.charAt(i) && arg.charAt(i) <= 'G') {
                    int letter = 0;
                    if ( 'A' <= arg.charAt(i) && arg.charAt(i) <= 'G') {
                         letter = arg.charAt(i) - 'A' + 10;
                    }
                    if (number == Integer.MAX_VALUE) {
                         number = letter;
                    } else {
                         number = 17 * number + letter;
                    }
                } else {
                    if (number != Integer.MAX_VALUE) {
                        number *= signOfNumber;
                        vectorRPN.add(number.toString());
                        signOfNumber = 1;
                        number = Integer.MAX_VALUE;
                        ++operatorOrNum;
                        lastCharWasNumber = true;
                    } 
                    if (arg.charAt(i) == '(') {
                        if (operatorOrNum > 0) {
                            throw (new Exception("Пропущен оператор!"));
                        }
                        if (operatorOrNum < 0) {
                            throw (new Exception("Пропущено число!"));
                        }
                        stack.push("(");
                        ++bracketBalance; 
                    } else {
                        if (arg.charAt(i) == ')') {
                            --bracketBalance;
                            if (bracketBalance < 0) {
                                throw (new Exception("Нарушен баланс скобок!"));
                            }
                            if (operatorOrNum < 1) {
                                throw (new Exception("Некорректный ввод. Возможно, пропущено число!"));
                            }
                            if (operatorOrNum > 1) {
                                throw (new Exception("Некорректный ввод. Возможно, пропущен оператор!"));
                            }
                            while (stack.peek() != "(") {
                                vectorRPN.add(stack.pop());
                            }
                             stack.pop();
                        } else {
                            switch (arg.charAt(i)) {
                                case '+':
                                    --operatorOrNum;
                                    if (operatorOrNum != 0) {
                                        throw (new Exception("Некорректный ввод. Возможно, пропущено число!"));
                                    }
                                    while ((!stack.empty()) && stack.peek() != "(" && (stack.peek() == "-" || stack.peek() == "+"
                                          || stack.peek() == "/" || stack.peek() == "*")) {
                                        vectorRPN.add(stack.pop());
                                    }
                                    stack.push("+");
                                    break;
                                case '-':
                                    System.out.println((vectorRPN.isEmpty()));
                                    boolean flag = !stack.empty();
                                    if (flag) {
                                        flag = (stack.peek() == "(");
                                    }
                                    if (vectorRPN.isEmpty() || (flag && !lastCharWasNumber)) {
                                        signOfNumber = -1;
                                    } else {
                                        --operatorOrNum;
                                        if (operatorOrNum != 0) {
                                            throw (new Exception("Некорректный ввод. Возможно, пропущено число!"));
                                        }
                                        while ((!stack.empty()) && (stack.peek() != "(") && (stack.peek() == "+" || stack.peek() == "-"
                                               || stack.peek() == "/" || stack.peek() == "*")) {
                                            vectorRPN.add(stack.pop());
                                            System.out.println("#");
                                        }
                                        stack.push("-");
                                    }
                                    break;
                                case '*':
                                    --operatorOrNum;
                                    if (operatorOrNum != 0) {
                                        throw (new Exception("Некорректный ввод. Возможно, пропущено число!"));
                                    }
                                    while ((!stack.empty()) && (stack.peek() == "/" || stack.peek() == "*") && stack.peek() != "(") {
                                        vectorRPN.add(stack.pop());
                                    }
                                    stack.push("*");
                                    break;
                                case '/':
                                    --operatorOrNum;
                                    if (operatorOrNum != 0) {
                                        throw (new Exception("Некорректный ввод. Возможно, пропущено число!"));
                                    }
                                    while ((!stack.empty()) && (stack.peek() == "*" || stack.peek() == "/") && stack.peek() != "(") {
                                        vectorRPN.add(stack.pop());
                                    }
                                    stack.push("/");
                                    break;
                                case' ': 
                                    break;
                                default:
                                    throw (new Exception("Неизвестный символ:" + arg.charAt(i)));
                            }
                            lastCharWasNumber = false;
                        }
                    }               
                }
            }
            ++i;              
        }
        if (bracketBalance != 0) {
            throw (new Exception("Нарушен баланс скобок!"));
        }
        if (number != Integer.MAX_VALUE) {
            number *= signOfNumber;
            vectorRPN.add(number.toString());
            if (stack.empty()){
                if (vectorRPN.size() != 1)
                throw (new Exception("Некорректный ввод. Возможно, пропущен оператор!"));
            }
        }
        while (!stack.empty()) {
            vectorRPN.add(stack.pop());
        }
        System.out.println(vectorRPN.toString());
        return vectorRPN;
    }
    
    private static String calculatorRPN( Vector<String> argument) throws Exception {
        int i = 0;
        Stack<Integer> stack = new Stack<Integer> ();
        while (i < argument.size()) {
            if (argument.elementAt(i).equals("+")
                    || argument.elementAt(i).equals("-")
                    || argument.elementAt(i).equals("*")
                    ||argument.elementAt(i).equals("/")) {
                Integer operand2;
                Integer operand1;
                try {
                    operand2 = stack.pop();
                    operand1 = stack.pop();
                } catch (Throwable e) {
                    throw (new Exception ("Непредвиденная ошибка"));
                }
                if (argument.elementAt(i).equals("+")) {
                    stack.push(operand1 + operand2);
                }
                if (argument.elementAt(i).equals("-")) {
                    stack.push(operand1 - operand2);
                }
                if (argument.elementAt(i).equals("*")) {
                    stack.push(operand1 * operand2);
                }
                if (argument.elementAt(i).equals("/")) {
                    stack.push(operand1 / operand2);
                }
            } else {
                Integer number;
                try {
                    number = Integer.parseInt(argument.elementAt(i));
                } catch (Exception e) {
                    throw (new Exception ("Непредвиденная ошибка"));
                }
                stack.push(number);
            }
            ++i;
        }
        String result = replacementNotation(stack.peek());
        return result;
    }
    
     private static String replacementNotation (int arg) {
        StringBuilder answer = new StringBuilder();
        int degree = 17;
        int counterDegree = 1;
        int digit = 0;
        if (arg < 0) {
            answer.append('-');
            arg = -arg;
        }
        while (arg > 0) {
            while (degree <= arg) {
                degree *= 17;
                ++counterDegree;
            }
            while (counterDegree > 0) {
                degree = degree / 17;
                --counterDegree;
                digit = arg / degree;
                switch (digit) {
                    case 10: 
                        answer.append('A');
                        break;
                    case 11:
                        answer.append('B');
                        break;
                    case 12:
                        answer.append('C');
                        break;
                    case 13:
                        answer.append('D');
                        break;
                    case 14:
                        answer.append('E');
                        break;
                    case 15:
                        answer.append('F');
                        break;
                    default:
                        answer.append(digit);
                }
                arg -= digit * degree;
            }
        }
        String result = answer.toString();
        if (result.isEmpty()) {
            return "0";
        } else {
            return result;
        }
    }   
  
}

