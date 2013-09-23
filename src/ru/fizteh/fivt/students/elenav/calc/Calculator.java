package ru.fizteh.fivt.students.elenav.calc;

import java.io.IOException;
import java.util.LinkedList;

public class Calculator {
	
	public static String convert17(int number10) {
		String result = new String();
		int tempNum = number10;
		while(tempNum > 0) {
			int mod = tempNum % 17;
			if(mod < 10)
				result = (char)('0' + mod) + result;
			else
				result = (char)('A' + mod - 10) + result;
			tempNum /= 17;
		}
		return result;
	}
	
	public static int priority(char operator) {
        switch(operator) {
            case '(': return 0;
            case ')': return 1;
            case '+': return 2;
            case '-': return 2;
            case '*': return 3;
            case '/': return 3;
            default: return 0;
        }
    }
	
    public static String toPostfix(String inputString) {
        StringBuilder sb = new StringBuilder();
    	String outputString;
        LinkedList<Character> operators = new LinkedList<>();
        int i = 0;
        while(i < inputString.length()) {
            char token = inputString.charAt(i);
            if((token >= '0' && token <='9') || (token >= 'A' && token <= 'G')) {
                sb.append(token);
                ++i;
                continue;
            }
            if(operators.isEmpty()) {
            sb.append(' ');
               operators.push(token);
               ++i;
               continue;
            }
            if(token == '(') {
                operators.push(token);
                ++i;
                continue;
            }
            if(token == ')') {
            	sb.append(' ');
                while(operators.getFirst() != '(' ) {
                    sb.append(operators.getFirst());
                    operators.pop();
                }
                operators.pop();
                ++i;
                continue;
            }
            sb.append(' ');
            while(!operators.isEmpty() && (priority(operators.getFirst()) >= priority(token))) {
            	sb.append(operators.getFirst());
                operators.pop();
            }
            operators.push(token);
            ++i;
        }
        sb.append(' ');
        while(!operators.isEmpty()) {
            sb.append(operators.pop());
        }
        outputString = sb.toString();
        return outputString;
    }
	
	public static int getNumber(String convertString, int i) {
	        int num = 0;
	        int length = convertString.length()-1;
	        while(i < length && convertString.charAt(i) != ' ') {
	            if(convertString.charAt(i) >= '0' && convertString.charAt(i) <= '9')
	                num = num * 17 + convertString.charAt(i) - '0';
	            else
	                num = num * 17 + convertString.charAt(i) - 'A' + 10;
	            ++i;
	        }
	        return num;
	    }
	
    public static int calculate(String convertString) {
        int result = 0;
        LinkedList<Integer> numbers = new LinkedList<>();
        for (int i = 0; i < convertString.length(); ++i) {
            char token = convertString.charAt(i);
            if((token >= '0' && token <='9') || (token >= 'A' && token <= 'G')) {
                int temp = getNumber(convertString, i);
                numbers.push(temp);
                continue;
            }
            if(token == '+') {
                int temp = numbers.pop() + numbers.pop();
                numbers.push(temp);
                continue;
            }
            if(token == '/') {
                int temp = numbers.pop();
                temp =  numbers.pop() / temp;
                numbers.push(temp);
                continue;
            }
            if(token == '-') {
                int temp = numbers.pop() - numbers.pop();
                numbers.push(-temp);
                continue;
            }
            if(token == '*') {
                int temp = numbers.pop() * numbers.pop();
                numbers.push(temp);
                continue;
            }
        }
        return numbers.getLast();
    }

	public static void main(String[] args) throws IOException {
		StringBuilder sb = new StringBuilder();
		for (String s : args) {
			sb.append(s);
		}
		String expression = sb.toString();
		
		String convertString = toPostfix(expression);
		int result = calculate(convertString);
		String outStr = convert17(result);
		System.out.println(outStr);
		
		System.exit(0);
	}
}