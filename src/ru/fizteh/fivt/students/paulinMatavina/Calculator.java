package ru.fizteh.fivt.students.paulinMatavina;
//java -classpath target ru.fizteh.students.paulinMatavina.Main

import java.util.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.*;


public class Calculator {
	//get priority for an operator
	private static int getPriority(char oper) {
		switch (oper) {
			case '(' : return -1;
			//case ')' : some special case 
			case '+' : return 2;
			case '-' : return 2;
			case '*' : return 1;
			case '/' : return 1;
			default: System.out.format("Failed. Wrong symbol %c\n");
				 System.exit(1);
		}
		return 0;
	}
	
	private static int tryPopInt(Stack<Integer> numStack) {
		if (numStack.empty()) {
			System.out.println("Error: incorrect expression");
			System.exit(1);
		}
		return numStack.pop();
	}
	//
	private static void makeOperation(Stack<Character> operStack, Stack<Integer> numStack) {
		if (operStack.empty()) {
			System.out.println("Error: incorrect expression");
			System.exit(1);
		}
		char ch = operStack.pop();
		if (ch == '(') {
			operStack.push(ch);
			return;
		}
		int a = tryPopInt(numStack);
		//System.out.print(a);
		//System.out.print(ch);
		int b = tryPopInt(numStack);
		//System.out.print(b);
		switch (ch) {
			case '+' : numStack.push(a + b);
				   break;
			case '-' : numStack.push(a - b);
				   break;
			case '*' : numStack.push(a * b);
				   break;
			case '/' : if (b == 0) {
					System.out.println("Error: division by zero!");
					System.exit(1);
				   }
				   numStack.push(a / b);
				   break;
		}
		//System.out.print("=");
		//System.out.println(numStack.peek());
	}

	//main	
	public static void main(String[] args) {
		String exp = "(";
		for (int i = 0; i < args.length; i++) { 
			String[] part = args[i].split(" ");
			for (int j = 0; j < part.length; j++) {
				exp += part[j];
			}
		}
		exp += ')';
		//System.out.println(exp);

		Stack<Integer> numStack = new Stack<Integer>();
		Stack<Character> operStack = new Stack<Character>();

		int i = 0;
		while (i < exp.length()) {
			String number = "";
			while ((i < exp.length()) &&
					(Character.isDigit(exp.charAt(i)) || 
					 	(exp.charAt(i) >= 'A' && exp.charAt(i) <= 'G'))) {
				number += exp.charAt(i);
				i++;
			}
			if (number != "") {
				numStack.push(Integer.parseInt(number, 17));
			}

				
			if (i >= exp.length()) 
				break;
			char ch = exp.charAt(i);
			int prior;
			switch (ch) {
				case '(' :   
					operStack.push(ch);
					break;
				case ')' :   
					prior = getPriority(operStack.peek());
					while (prior > 0) {
						prior = getPriority(operStack.peek());
						makeOperation(operStack, numStack);
					}
					operStack.pop();
					break;
				case '+':
				case '-':
				case '*':
				case '/':	
					prior = getPriority(operStack.peek());
					while (prior > 0 && prior <= getPriority(ch)) {
						prior = getPriority(operStack.peek());
						makeOperation(operStack, numStack);
					}
					operStack.push(ch);
					break;
				default:	
					System.out.format("Failed. Wrong symbol %c\n", ch); 
					System.exit(1);
			}
			i++;
		}
		
		if (!operStack.empty()) {
			System.out.println("Error: incorrect expression!");
			System.exit(1);
		}
		System.out.println(Integer.toString(numStack.pop(), 17).toUpperCase());
	}
}
