// Nicolai Kochetov, 294, calculator

package ru.fizteh.fivt.students.kochetovnicolai.calculator;

import java.util.Stack;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Calculator {
    
    private static int nextNumber;
    private static int position;

    private static void printSubExpression(String operations, String[] numbers) {
        int nextNumber = 1;
        for (int i = 0; i < position; i++) {
            if (operations.charAt(i + 1) == ' ') {
                System.err.print(numbers[nextNumber++]);
            } else {
                System.err.print(operations.charAt(i + 1));
            }
        }
        System.err.println();
    }
    
    private static void terminate(String error) {
        System.err.println(error);
        System.exit(1);
    }
    
    private static int calculate(String operations, String[] numbers) {
        Stack<Integer> values = new Stack<>();
        values.push(0);
        char operation = '+';
        position++;
        while (true) {
            if (!" (".contains(operations.subSequence(position, position + 1))) {
                printSubExpression(operations, numbers);
                terminate("Expected constant or (, but \'" + operations.charAt(position) + "\' found");
            }
            int value = 0;
            if (operations.charAt(position) == ' ') {
                try {
                    value = Integer.valueOf(numbers[nextNumber++], 18);
                } catch (NumberFormatException e) {
                    System.err.println("Error: Too long number");
                    terminate(e.getMessage());
                }
            } else {
                value = calculate(operations, numbers);
            }
            
            if (operation == '+') {
                values.push(value);
            } else if (operation == '-') {
                values.push(-value);
            } else if (operation == '*') {
                Long multiplication = values.pop().longValue() * value;
                if (multiplication > Integer.MAX_VALUE || multiplication < Integer.MIN_VALUE) {
                    terminate("result of an expression out of range");
                }
                values.push(multiplication.intValue());
            } else if (operation == '/') {
                if (value == 0) {
                    printSubExpression(operations, numbers);
                    terminate("Division by zero");
                }
                Integer previos = (Integer) values.pop();
                values.push(previos / value);
            }
            
            position++;
            if (position == operations.length()) {
                terminate("Unexpected end of expression. Expected +, -, *, / or )");
            }
            if (operations.charAt(position) == ')') {
                break;
            }
            
            if (!"-+*/".contains(operations.subSequence(position, position + 1))) {
                printSubExpression(operations, numbers);
                terminate("Expected operation +, -, * or / after constant, but \'" + 
                        operations.charAt(position) + "\' found");
            }
            operation = operations.charAt(position);
            
            position++;
            if (position == operations.length()) {
                terminate("Unexpected end of expression. Expected constant or ( ");
            }
        }
        Long result = new Long(0);
        while (!values.empty()) {
            result += values.pop();
            if (result > Integer.MAX_VALUE || result < Integer.MIN_VALUE) {
                terminate("result of an expression out of range");
            }
        }
        return result.intValue();
    }
        
    public static void main(String[] args) {
        
        if(args.length == 0)
            terminate("Usage: java Calculator [arithmetic expression]");
        
        StringBuilder builder = new StringBuilder("(");
        for (int i = 0; i < args.length; i++) {
            builder.append(args[i]);
        }
        builder.append(")");
        String expression = builder.toString();
        
        Matcher matcher = Pattern.compile("[0-9A-Ha-h][ ]+[0-9A-Ha-h]").matcher(expression);
        if (matcher.find()) {
            System.err.println(expression.substring(1, matcher.start() + 4));
            terminate("Expected operation +, -, * or / after constant, but \' \' found");
        }
                
        expression = expression.replace(" ", "");
        String[] numbers = expression.split("[^0-9A-Ha-h]+");
        String operations = expression.replaceAll("[0-9a-hA-H]+", " ");
        
        position = 0;
        nextNumber = 1;
        System.out.println(Integer.toString(calculate(operations, numbers), 18));
    }
}