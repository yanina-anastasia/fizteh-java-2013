package ru.fizteh.fivt.students.demidov.calculator;

import java.io.IOException;
import java.util.Deque;
import java.util.ArrayDeque;

public class calculator {
	private static Deque<Integer> numbers = new ArrayDeque<Integer>();	
	private static Deque<String> operations = new ArrayDeque<String>();
	
	public static int GetOperand() throws IOException {
		if (numbers.isEmpty()) {
			throw new IOException("wrong expression");
		}
		return numbers.pop();
	}
	
	public static void CountOperation() throws IOException {
		String operation; 		
		if (!(operations.isEmpty())) {
			operation = operations.pop();
		} else {
			throw new IOException("wrong expression");
		}
		
		if (operation.equals("(")) {
			throw new IOException("mismatched quotes");
		}
		
		int secondOperand = GetOperand();
		int firstOperand = GetOperand();
		
		if (operation.equals("+")) {
			if (((secondOperand <= 0) && (firstOperand >= 0)) || ((secondOperand >= 0) && (firstOperand <= 0)) 
					|| (Integer.MAX_VALUE - Math.abs(secondOperand) >= Math.abs(firstOperand))) {
				numbers.push(firstOperand + secondOperand);
			} else {
				throw new IOException("Integer overflow");
			}
		} else if (operation.equals("-")) {
			if (((secondOperand >= 0) && (firstOperand >= 0)) || ((secondOperand <= 0) && (firstOperand <= 0)) 
					|| (Integer.MAX_VALUE - Math.abs(secondOperand) >= Math.abs(firstOperand))) {
				numbers.push(firstOperand - secondOperand);
			} else {
				throw new IOException("Integer overflow");
			}
		} else if (operation.equals("*")) {
			if ((secondOperand == 0) || (Integer.MAX_VALUE / Math.abs(secondOperand) >= Math.abs(firstOperand))) {
				numbers.push(firstOperand * secondOperand);
			} else {
				throw new IOException("Integer overflow");
			}
		} else if (operation.equals("/")) {
			if (secondOperand != 0) {
				numbers.push(firstOperand / secondOperand);
			} else {
				throw new IOException("/ by zero");
			}
		}
	}
	
	public static int GetPriority(String operation) {
		if (operation.matches("[+-]")) {
			return 1;
		} else if (operation.matches("[*/]")) {
			return 2;
		} else {
			return 0;
		}
	}

	public static void main(String[] args) {
		for (int i = 0; i < args.length; ++i) {
			int posInArgument = 0;
			while (posInArgument < args[i].length()) {
				String currentToken = Character.toString(args[i].charAt(posInArgument));	
				if (currentToken.matches("[0-9A-Ia-i]")){
					while ((posInArgument + 1 < args[i].length()) 
							&& (Character.toString(args[i].charAt(posInArgument + 1)).matches("[0-9A-Ia-i]"))) {
						currentToken += Character.toString(args[i].charAt(posInArgument + 1));
						++posInArgument;
					}
					try {					
						numbers.push(Integer.parseInt(currentToken, 19));
					} catch(NumberFormatException catchedException) {
						System.err.println(catchedException);
						return;
					}
				} else if (currentToken.equals("(")) {
					operations.push("(");
				} else if (currentToken.equals(")")) {
					while (!(operations.isEmpty()) && !(operations.peek().equals("("))) {
						try {
							CountOperation();
						} catch(IOException catchedException) {
							System.err.println(catchedException);
							return;
						}
					}
					if (!(operations.isEmpty())) {
						operations.pop();
					} else {
						System.err.println("mismatched quotes");
						return;
					}
				} else if (currentToken.matches("[*/+-]")) {
					while (!(operations.isEmpty()) && (GetPriority(operations.peek()) >= GetPriority(currentToken))) {
						try {
							CountOperation();
						} catch(IOException catchedException) {
							System.err.println(catchedException);
							return;
						}
					}
					operations.push(currentToken);
				} else if (!currentToken.equals(" ")) {
					System.err.println("wrong expression");
					return;
				}
				
				++posInArgument;
			}
		}
		
		while (!operations.isEmpty()) {
			try {
				CountOperation();
			} catch(IOException catchedException) {
				System.err.println(catchedException);
				return;
			}
		}
		
		if (numbers.isEmpty()) {
			System.err.println("wrong expression");
			return;	
		} else {
			Integer result = numbers.pop();
			if (!numbers.isEmpty()) {
				System.err.println("wrong expression");
				return;	
			} else {
				System.out.println(Integer.toString(result, 19));	
			}			
		}
	}
}