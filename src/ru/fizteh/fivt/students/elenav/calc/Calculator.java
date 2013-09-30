package ru.fizteh.fivt.students.elenav.calc;

import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;

public class Calculator {
	
    public static int priority(char operator) {
        switch(operator) {
            case '(': return 0;
            case ')': return 1;
            case '+': return 2;
            case '-': return 2;
            case '*': return 3;
            case '/': return 3;
            default: return -1;
        }
    }
	
    public static String toPostfix(String inputString) throws IOException {
        StringBuilder sb = new StringBuilder();
        Deque<Character> operators = new LinkedList<>();
        int i = 0;
        while(i < inputString.length()) {
            char token = inputString.charAt(i);
            //System.out.println(token);
            if((token >= '0' && token <='9') || (token >= 'A' && token <= 'G')) {
                sb.append(token);
                ++i;
                continue;
            }
            if(operators.isEmpty() && priority(token) != -1) {
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
            if(priority(token) != -1) {
            	sb.append(' ');
            	while(!operators.isEmpty() && (priority(operators.getFirst()) >= priority(token))) {
            		sb.append(operators.getFirst());
            		operators.pop();
            	}
            	operators.push(token);
            	++i;
            } else
            	throw new IOException("invalid input");
        }
        sb.append(' ');
        while(!operators.isEmpty()) {
        	if(operators.getFirst() == '(' || operators.getFirst() == ')')
        	   throw new IOException("invalid input");
            sb.append(operators.pop());
        }   
        return sb.toString();
    }
	
	public static int getNumber(String convertString, int i) {
	        int num = 0;
	        int length = convertString.length()-1;
	        while(i < length && convertString.charAt(i) != ' ') {
	            if(convertString.charAt(i) >= '0' && convertString.charAt(i) <= '9') {
	                num = num * 17 + convertString.charAt(i) - '0';
	            }
	            else {
	                num = num * 17 + convertString.charAt(i) - 'A' + 10;
	            }
	            ++i;
	        }
	        return num;
	    }
	
    public static int calculate(String convertString) throws IOException {
        LinkedList<Integer> numbers = new LinkedList<>();
        for (int i = 0; i < convertString.length(); ++i) {
            char token = convertString.charAt(i);
            if((token >= '0' && token <='9') || (token >= 'A' && token <= 'G')) {
                int temp = getNumber(convertString, i);
                while(convertString.charAt(i) != ' ')
                	++i;
                numbers.push(temp);
                continue;
            }
            if(numbers.size() < 2 && priority(token) != -1)
            	throw new IOException("invalid input");
            if(token == '+') {
            	int x1 = numbers.pop();
            	int x2 = numbers.pop();
            	if(Integer.MAX_VALUE - x1 < x2) {
            		throw new IOException("more than max int");
            	}
                numbers.push(x1 + x2);
                continue;
            }
            if(token == '/') {
                int temp = numbers.pop();
                temp =  numbers.pop() / temp;
                numbers.push(temp);
                continue;
            }
            if(token == '-') {
            	int x1 = numbers.pop();
            	int x2 = numbers.pop();
            	if( Integer.MIN_VALUE + x1 > x2) {
            		throw new IOException("more than max int");
            	}
               numbers.push(x2 - x1);
                continue;
            }
            if(token == '*') {
            	int x1 = numbers.pop();
            	int x2 = numbers.pop();
            	if( Integer.MAX_VALUE / x1 < x2) {
            		throw new IOException("more than max int");
            	}
                numbers.push(x1 * x2);
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
		System.out.println(expression);
		
		try{
			String convertString = toPostfix(expression);
			//System.out.println(convertString);
			int result = calculate(convertString);
			String outStr = Integer.toString(result, 17);
			System.out.println(outStr);
		} catch (IOException err) {
			System.out.println(err); 
			System.exit(1);
		}
		System.exit(0);
	}
}