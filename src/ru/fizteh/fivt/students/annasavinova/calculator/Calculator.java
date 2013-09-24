package ru.fizteh.fivt.students.annasavinova.calculator;

import java.util.Scanner;
import java.util.Stack;

public class Calculator {
	static Stack<String> signStack = new Stack<>();
	static Stack<Integer> digitStack = new Stack<>();
	public static int priority(String c) {
		if (c.intern() == ("+").intern() || c.intern() == ("-").intern()) {
			return 0;
		} else if (c.intern() == ("*").intern() || c.intern() == ("/").intern()) {
				return 1;
			} else {
				return -1;
			}
	}
	public static int getVal() {
		String sign = signStack.pop();
		int secondVal = digitStack.pop();
		int firstVal = digitStack.pop();
		int res = 0;
		switch (sign) {
			case "+":
				res = firstVal + secondVal;
				break;
			case "-":
				res = firstVal - secondVal;
				break;
			case "*":
				res = firstVal * secondVal;
				break;
			case "/":
				res =  firstVal / secondVal;
				break;
		}
		return res;
	}
	public static boolean checkBrackets(String input) {
		int count = 0;
		for (int i = 0; i < input.length(); ++i) {
			if (input.charAt(i) == '(') {
				++count;
			} else if (input.charAt(i) == ')') {
				--count;
			}
			if (count < 0) {
				return false;
			}
		}
		if (count != 0) {
			return false;
		}
		return true;
	}
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Please, input expression, that you want to know\nAcceptable symbols: +, -, *, /\nRadix: 18");
			return;
		}
		String str = "";
		for (int i = 0; i < args.length; ++i) {
			str += args[i];
		}
		str = str.replace(" ", "");
		if (!checkBrackets(str)) {
			System.out.println("Incorrect bracket sequance");
			return;
		}
		str = str.replace("(", "( ");
		str = str.replace(")", " )");
		str = str.replace("+", " + ");
		str = str.replace("-", " - ");
		str = str.replace("*", " * ");
		str = str.replace("/", " / ");
		str = "( " + str + " )";
		Scanner sc = new Scanner(str);
		sc.useRadix(18);
		sc.useDelimiter(" ");
		while (sc.hasNext()) {
			if (sc.hasNext("[/(/)/+-/*//]")) {
				String currentSymbol = sc.next();
				switch (currentSymbol) {
				case "(": 
					signStack.push(currentSymbol);
					break;
				case ")":
					while ((signStack.peek()).intern() != ("(").intern()) {
							digitStack.push(getVal());
					}
					signStack.pop();
					break;
				default:
					if (signStack.empty()
							|| priority(signStack.peek()) < priority(currentSymbol)) {
						signStack.push(currentSymbol);
					} else {
						while (priority(signStack.peek()) >= priority(currentSymbol)) {
								digitStack.push(getVal());
						}
						signStack.push(currentSymbol);
					}
				}
			} else {
				if (sc.hasNextInt()) {
					int currentDigit = sc.nextInt();
					digitStack.push(currentDigit);
				} else {
					System.out.println("Incorrect expression: incorrect symbol or num of operators and operands");
					sc.close();
					return;
				}
			}
		}
		int result = digitStack.pop();
		if (digitStack.empty()) {
			System.out.println("Result: " + result);			
		} else {
			System.out.println("Incorrect num of operators and operands");
		}
		sc.close();
	}
}