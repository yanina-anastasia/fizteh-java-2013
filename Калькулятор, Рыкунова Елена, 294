package ru.fizteh.fivt.students.RykunovaElena.calculator;

import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Pattern;

public class Calculator {
	
	static Stack <Long> operands = new Stack <> ();
	static Stack <String> operators = new Stack <> ();
	
	private static boolean isCorrectExpression() {
		if (operands.size() < 2 || operators.size() < 1) {
			System.err.println("Wrong expression.");
			return false;
		}
		return true;
	}
	
	private static void makeCalculation() {
		// помним об обратном порядке!
		long secondOperand = operands.pop();
		long firstOperand = operands.pop();
		String operator = operators.pop();
		
		long result = 0;
		
//		System.out.println("I get: " + secondOperand + operator + firstOperand);
		
		if (operator.equals("+")) {
			result = firstOperand + secondOperand;
		} else if (operator.equals("-")) {
				result = firstOperand - secondOperand;
			} else if (operator.equals("*")) {
					result = firstOperand * secondOperand;
				} else if (operator.equals("/")) {
						result = firstOperand / secondOperand;
					}

		operands.push(result);
//		System.out.println("I calc: " + result);
	}
	
	private static boolean isCorrectBracketSeq (String str) {
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
		if (Pattern.matches("[0-9a-gA-G\\s/(/)/+/-/*//]*([0-9a-zA-Z]([\\s]+)[0-9a-zA-Z])+[0-9a-gA-G\\s/(/)/+/-/*//]*", str)) {
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
	
	public static void main (String [] args) {
		if (args.length < 1) {
			System.err.println("No expression is found. Use 0-9, A-G, a-g, +, -, *, /, (, ) to write an expression that you want to calc.");
			return;
		}
	
		int base = 17; 

		StringBuffer s = new StringBuffer();
		for (int i = 0; i < args.length; i++) {
			s.append(args[i]);
		}
		String str = s.toString();

		// проверка на корректность входа
		// 1) проверка на соответствие символов
		if (!Pattern.matches("[0-9a-gA-G\\s/(/)/+/-/*//]*", str)) {
			System.err.println("Unexpected symbol. Use 0-9, A-G, a-g, +, -, *, /, (, ).");
			return;
		}
		// 2) проверка на правильную скобочную последовательность
		if (!isCorrectBracketSeq(str)) {
			System.err.println("Wrong expression: brackets mismatch.");
			return;
		}
		// 3) проверка на лишние пробелы (не должно быть пробелов между двумя цифрами)
		if (!isCorrectSpaceSeq(str)) {
			System.err.println("Wrong expression: too many spaces.");
			return;
		}
		
		// убираем все пробелы
		str = str.replace(" ", "");
		
		Scanner myScan = new Scanner(str);
		myScan.useRadix(base);

		// разделяем на операнды и операции. Разделение будет происходить на стыке цифры и операции, операции и цифры, операции и операции
		myScan.useDelimiter("((?<=[0-9a-gA-G])(?=[/(/)/+/-/*//]))|((?<=[/(/)/+/-/*//])(?=[0-9a-gA-G]))|((?<=[/(/)/+/-/*//])(?=[/(/)/+/-/*//]))"); 		

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
							if (!isCorrectExpression()) {
								myScan.close();
								return;
							} else {
								makeCalculation();
							}
						}
						if (!operators.isEmpty()) {
							operators.pop();  // достали последнюю открывающую скобку
						} else {
							System.err.println("Wrong expression"); // хотя такого не должно быть, 
							myScan.close();							// т.к. мы проверили скобочную последовательность на правильность
							return;
						}
					} else {
						// для других операций достаем из стека все с приоритетом меньше нашего 
						// и производим вычисления, потом добавляем операцию в стек
						currPriority = getPriority(currOperator);
						while (!operators.isEmpty() && getPriority(operators.peek()) >= currPriority) {
							if (!isCorrectExpression()) {
								myScan.close();
								return;
							} else {
								makeCalculation();
							}
						}
						operators.push(currOperator);
					}	
			}
		}
		
		while (!operators.isEmpty()) {
			if (!isCorrectExpression()) {
				myScan.close();
				return;
			} else {
				makeCalculation();
			}		
		}
		
		if (operands.size() != 1) {
			System.err.println("Wrong expression");			
		} else {
			long result = operands.pop();
			System.out.println(Long.toString(result, base));
		}
		myScan.close();
	}
}
