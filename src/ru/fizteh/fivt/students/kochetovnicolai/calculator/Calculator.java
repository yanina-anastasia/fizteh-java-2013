// Nicolai Kochetov, 294, calculator

package ru.fizteh.students.kochetovnicolai.calculator;

import java.util.Stack;

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
    
    private static int calculate(String operations, String[] numbers) {
        Stack values = new Stack();
        values.push(0);
        char operation = '+';
        position++;
        while (true) {
            if (!" (".contains(operations.subSequence(position, position + 1))) {
                printSubExpression(operations, numbers);
                System.err.print("Expected constant or (, ");
                System.err.println("but \'" + operations.charAt(position) + "\' found");
                System.exit(1);
            }
            int value;
            if (operations.charAt(position) == ' ') {
                value = Integer.valueOf(numbers[nextNumber++], 18);
            } else {
                value = calculate(operations, numbers);
            }
            
            if (operation == '+') {
                values.push(value);
            } else if (operation == '-') {
                values.push(-value);
            } else if (operation == '*') {
                Integer previos = (Integer) values.pop();
                values.push(previos * value);
            } else if (operation == '/') {
                if (value == 0) {
                    printSubExpression(operations, numbers);
                    System.err.println("Division by zero");
                    System.exit(1);
                }
                Integer previos = (Integer) values.pop();
                values.push(previos / value);
            }
            
            position++;
            if (position == operations.length()) {
                System.err.print("Unexpected end of expression. ");
                System.err.println("Expected +, -, *, / or )");
                System.exit(1);
            }
            if (operations.charAt(position) == ')') {
                break;
            }
            
            if (!"-+*/".contains(operations.subSequence(position, position + 1))) {
                printSubExpression(operations, numbers);
                System.err.print("Expected operation +, -, * or / after constant, ");
                System.err.println("but \'" + operations.charAt(position) + "\' found");
                System.exit(1);
            }
            operation = operations.charAt(position);
            
            position++;
            if (position == operations.length()) {
                System.err.print("Unexpected end of expression. ");
                System.err.println("Expected constant or (");
                System.exit(1);
            }
        }
        int result = 0;
        while (!values.empty()) {
            result += (Integer) values.pop();
        }
        return result;
    }
        
    public static void main(String[] args) {
        StringBuilder builder = new StringBuilder("(");
        for (int i = 0; i < args.length; i++) {
            builder.append(args[i]);
        }
        builder.append(")");
        String expression = builder.toString().replace(" ", "");
        String[] numbers = expression.split("[^0-9A-Ha-h]+");
        String operations = expression.replaceAll("[0-9a-hA-H]+", " ");
        
        position = 0;
        nextNumber = 1;
        System.out.println(Integer.toString(calculate(operations, numbers), 18));
    }
}
