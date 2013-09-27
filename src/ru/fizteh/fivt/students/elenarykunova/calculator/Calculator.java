package ru.fizteh.fivt.students.elenarykunova.calculator;

import java.math.BigInteger;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Pattern;

public class Calculator {

    static Stack<Long> operands = new Stack<>();
    static Stack<String> operators = new Stack<>();
    
    private static boolean isCorrectExpression() {
        if (operands.size() < 2 || operators.size() < 1) {
            System.err.println("Wrong expression.");
            return false;
        }
        return true;
    }
    
    private static boolean makeCalculation() {
        // помним об обратном порядке!
        if (!isCorrectExpression()) {
            return false;
        }
        long secondOperand = operands.pop();
        long firstOperand = operands.pop();
        String operator = operators.pop();
        
        long result = 0;
        // проверяем на переполнение
        BigInteger firstBig = BigInteger.valueOf(firstOperand);
        BigInteger secondBig = BigInteger.valueOf(secondOperand);
        BigInteger realRes = null, myRes = null;
        
        if (operator.equals("+")) {
            result = firstOperand + secondOperand;
            realRes = firstBig.add(secondBig);
        } else if (operator.equals("-")) {
            result = firstOperand - secondOperand;
            realRes = firstBig.subtract(secondBig);
        } else if (operator.equals("*")) {
            result = firstOperand * secondOperand;
            realRes = firstBig.multiply(secondBig);
        } else if (operator.equals("/")) {
            if (secondOperand == 0) {
                System.err.println("Dividing by zero");
                return false;
            }
            result = firstOperand / secondOperand;
            realRes = BigInteger.valueOf(result);
        }
        
        myRes = BigInteger.valueOf(result);
        if (!realRes.equals(myRes)) {
            System.out.println("Too large values.");
            return false;
        }
        operands.push(result);
        return true;
    }
    
    private static boolean isCorrectBracketSeq(String str) {
        long count = 0;
        
        for (int i = 0; i < str.length(); i++) {
            if (count < 0) {
                return false;
            }
            if (str.charAt(i) == '(') {
                count++;
            }
            else
                if (str.charAt(i) == ')') {
                    count--;
                }
        }
    
        if (count != 0) {
            return false;
        }
        return true;
    }
    
    private static boolean isCorrectSpaceSeq(String str) {
        // если между двумя цифрами стоит хотя бы один пробел, то выражение неверно. 
        if (Pattern.matches("[0-9a-gA-G\\s/(/)/+-/*//]*([0-9a-zA-Z]([\\s]+)[0-9a-zA-Z])+[0-9a-gA-G\\s/(/)/+-/*//]*", str)) {
            return false;
        }
        return true;
    }
    
    private static int getPriority(String str) {
        // приоритет операций
        if (str.equals("(") || str.equals(")")) {
            return 1;
        }
        if (str.equals("+") || str.equals("-")) {
            return 2;
        }
        if (str.equals("*") || str.equals("/")) {
            return 3;
        }
        return -1;
    }
    
    public static void main(String [] args) {
        if (args.length < 1) {
            System.err.println("No expression is found. Use 0-9, A-G, a-g, +, -, *, /, (, ) to write an expression that you want to calc.");
            System.exit(1);
        }
    
        int base = 17; 

        StringBuffer s = new StringBuffer();
        for (int i = 0; i < args.length; i++) {
            s.append(args[i]);
            s.append(" ");
        }
        String str = s.toString();
        
        // проверка на корректность входа
        // 1) проверка на соответствие символов
        if (!Pattern.matches("[0-9a-gA-G\\s/(/)/+-/*//]*", str)) {
            System.err.println("Unexpected symbol. Use 0-9, A-G, a-g, +, -, *, /, (, ).");
            System.exit(1);
        }
        // 2) проверка на правильную скобочную последовательность
        if (!isCorrectBracketSeq(str)) {
            System.err.println("Wrong expression: brackets mismatch.");
            System.exit(1);
        }
        // 3) проверка на лишние пробелы (не должно быть пробелов между двумя цифрами)
        if (!isCorrectSpaceSeq(str)) {
            System.err.println("Wrong expression: too many spaces.");
            System.exit(1);
        }
        
        // убираем все пробелы
        str = str.replace(" ", "");
        
        Scanner myScan = new Scanner(str);
        myScan.useRadix(base);

        // разделяем на операнды и операции. Разделение будет происходить на стыке цифры и операции, операции и цифры, операции и операции
        myScan.useDelimiter("((?<=[0-9a-gA-G])(?=[/(/)/+-/*//]))|((?<=[/(/)/+-/*//])(?=[0-9a-gA-G]))|((?<=[/(/)/+-/*//])(?=[/(/)/+-/*//]))");         

        // собственно начинаем вычисления
        int currPriority = 0;
        String currOperator;
        while (myScan.hasNext()) {
            if (myScan.hasNextLong()) {
                // получили операнд - добавили в стек операндов
                long newInt = myScan.nextLong();
                operands.push(newInt);
            } else {
                currOperator = myScan.next();
                if (currOperator.equals("(")) {
                    // получили ( - добавили в стек операций
                    operators.push(currOperator);
                } else if (currOperator.equals(")")) {
                    // получили ) -
                    // пока не наткнемся на открывающую скобку, достаем из стека операции и тут же вычисляем
                    while (!operators.isEmpty() && !operators.peek().equals("(")) {
                        if (!makeCalculation()) {
                            myScan.close();
                            System.exit(1);
                        }
                    }
                    if (!operators.isEmpty()) {
                        operators.pop();  // достали последнюю открывающую скобку
                    } else {
                        System.err.println("Wrong expression: '(' not found"); // хотя такого не должно быть, 
                        myScan.close();                            // т.к. мы проверили скобочную последовательность на правильность
                        System.exit(1);
                    }
                } else if (currOperator.equals("+") || currOperator.equals("-") || currOperator.equals("*") || currOperator.equals("/")) {
                    currPriority = getPriority(currOperator);
                    while (!operators.isEmpty() && getPriority(operators.peek()) >= currPriority) {
                        if (!makeCalculation()) {
                            myScan.close();
                            System.exit(1);
                        }
                    }
                    operators.push(currOperator);
                } else {
                    System.err.println("Too large number.");
                    myScan.close();
                    System.exit(1);
                }
                    // для других операций достаем из стека все с приоритетом не меньше нашего 
                    // и производим вычисления, потом добавляем операцию в стек
                     
            }
        }
        
        while (!operators.isEmpty()) {
            if (!makeCalculation()) {
                myScan.close();
                System.exit(1);
            }        
        }
        
        if (operands.size() != 1) {
            System.err.println("Wrong expression.");
            myScan.close();
            System.exit(1);
        } else {
            long result = operands.pop();
            System.out.println(Long.toString(result, base));
        }
        myScan.close();
    }
}
