package ru.fizteh.fivt.students.msandrikova.calculator;

import java.util.*;

public class Calculator {
	
	private static boolean isMathSymbol(char s) {
		return(s == '+' || s == '-' || s == '*' || s == '/');
	}

	private static boolean isInt(String s, int radix) {
		try {
			Integer.parseInt(s, radix);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
	
	private static boolean isBracket(char s) {
		return(s == '(' || s == ')');
	}
	
	public static void main(String[] args) {
		int RAD = 17; 
		Queue<String> polishNotation = new LinkedList<String>();
		Stack<String> tempForPolishNotation = new Stack<String>();
		String token = new String();
		String expression = new String();
		
		if(args.length == 0) {
			System.out.println("Error: Your should enter at least one argument");
			return;
		}
		
		for(String s : args) {
			expression += s;
		}
		
		expression = expression.replace(" ", "");
		
		Vector<String> tokens = new Vector<String>();
		
		
		for(int i = 0; i < expression.length(); i++) {
			if(Calculator.isBracket(expression.charAt(i)) || Calculator.isMathSymbol(expression.charAt(i))) {
				if(token.length() != 0) {
					if(Calculator.isInt(token, RAD)){
						tokens.add(token);
						token = "";
					} else {
						System.out.println("Error: Illigal symbol in " + token );
						return;
					}
				}
				token += expression.charAt(i);
				tokens.add(token);
				token = "";
				continue;
			} else {
				token += expression.charAt(i);
			}
		}
		if(token.length() != 0) {
			if(Calculator.isInt(token, RAD)){
				tokens.add(token);
				token = "";
			} else {
				System.out.println("Error: Illigal symbol in " + token );
				return;
			}
		}
		
		for(String s : tokens) {
			token = s;
			if(Calculator.isInt(token, RAD)) {
				polishNotation.add(token);
				continue;
			} else if(token.equals("(")) {
				tempForPolishNotation.add(token);
				continue;
			} else if(token.equals(")")) {
				while(!tempForPolishNotation.empty() && !tempForPolishNotation.peek().equals("(")) {
					polishNotation.add(tempForPolishNotation.pop());
				}
				if(tempForPolishNotation.empty()){
					System.out.println("Error: Brackets isn't balanced");
					return;
				} else {
					tempForPolishNotation.pop();
				}
				continue;
			} else {
				if(token.matches("[+-]")) {
					while(!tempForPolishNotation.empty() && tempForPolishNotation.peek().matches("[*+-/]")) {
						polishNotation.add(tempForPolishNotation.pop());
					}
					tempForPolishNotation.push(token);
				} else if(token.matches("[*/]")) {
					while(!tempForPolishNotation.empty() && tempForPolishNotation.peek().matches("[*/]")) {
						polishNotation.add(tempForPolishNotation.pop());
					}
					tempForPolishNotation.push(token);
				} 
			}
		}
				
		while(!tempForPolishNotation.empty() && tempForPolishNotation.peek().matches("[*+-/]")) {
			polishNotation.add(tempForPolishNotation.pop());
		}
		if(!tempForPolishNotation.empty()) {
			System.out.println("Brackets isn't balanced");
			return;
		}
		
		Stack<Integer> calcOfPolishNotation = new Stack<Integer>();
		while(polishNotation.peek() != null) {
			token = polishNotation.remove();
			if(Calculator.isInt(token, RAD)) {
				calcOfPolishNotation.push(Integer.parseInt(token, RAD));
			} else {
				Integer a = 0, b = 0, res = 0;
				a = calcOfPolishNotation.pop();
				b = calcOfPolishNotation.pop();
				switch(token) {
				case "+":
					res = b + a;
					break;
				case "-":
					res = b - a;
					break;
				case "/":
					if(a.equals(0)) {
						System.out.println("Incorret expression: division by zero");
						return;
					}
					res = b / a;
					break;
				case "*":
					res = b * a;
					break;
				}
				calcOfPolishNotation.push(res);
			}
		}
		System.out.println(Integer.toString(calcOfPolishNotation.pop(), RAD));
	}

}
