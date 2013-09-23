package ru.fizteh.fivt.students.DZvonarev.Calc;

import java.util.Stack;

class Calc {

	public static String ConcatStrings(String[] strings) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < strings.length; ++i) {
			sb.append(strings[i]);
		}
		String expression = sb.toString();
		return expression;
	}

	public static int Priority(String str) {
		if (str.equals("+") || str.equals("-")) {
			return 1;
		}
		if (str.equals("*") || str.equals("/")) {
			return 2;
		}
		if (str.equals("(")) {
			return 0;
		}
		if (!str.equals("*") && !str.equals("-") && !str.equals("/")
				&& !str.equals("+") && !str.equals("(")) {
			System.err.println("Wrong operation");
			System.exit(1);
		}
		return 0;
	}

	public static int DoOp(int num1, String op, int num2) {
		if (op.equals("+"))
			return num1 + num2;
		if (op.equals("*"))
			return num1 * num2;
		if (op.equals("-"))
			return num1 - num2;
		if (op.equals("/") && num2 != 0) {
			return num1 / num2;
		} else {
			System.err.println("Division by zero");
			System.exit(1);
		}
		return 0;
	}

	private static Stack<String> number = new Stack();
	private static Stack<String> operation = new Stack();

	public static void CurrentOperation() {
		int num1 = Integer.parseInt(number.pop(), 19);
		int num2 = Integer.parseInt(number.pop(), 19);
		String op = operation.pop();
		int num = DoOp(num2, op, num1);
		String new_str = Integer.toString(num, 19);
		number.push(new_str);
	}

	public static void main(String[] args) {

		for (int i = 0; i < args.length; ++i) {
			if (args[i]
					.matches("[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, A, B, C, D, E, F, G, H, I]*")) {
				number.push(args[i]);
			} else {
				if (!args[i].equals("+") && !args[i].equals("-")
						&& !args[i].equals("*") && !args[i].equals("/")
						&& !args[i].equals("(") && !args[i].equals(")")) {
					System.err.println("Wrong expression");
					System.exit(1);
				}
			}

			if (args[i].equals("(")) {
				operation.push(args[i]);
			}

			if (args[i].equals(")")) {
				if (!operation.isEmpty()) {
					while (!operation.peek().equals("(")) {
						CurrentOperation();
						if (operation.isEmpty()) {
							System.err.println("Wrong bracket sequence");
							System.exit(1);
						}
					}
					operation.pop(); // we pop open clause
				} else {
					System.err.println("Wrong bracket sequence");
					System.exit(1);
				}
			}

			if (args[i].equals("+") || args[i].equals("-")
					|| args[i].equals("*") || args[i].equals("/")) {
				if (operation.isEmpty()) {
					operation.push(args[i]);
					continue;
				}
				if (Priority(operation.peek()) < Priority(args[i])) {
					operation.push(args[i]);
					continue;
				}
				// now we do if op's bigger priority then now
				while (Priority(operation.peek()) >= Priority(args[i])) {
					if (number.size() < 2) {
						System.err.print("I can't do ");
						System.err.print(args[i]);
						System.err.println(" operation");
						System.exit(1);
					}
					CurrentOperation();
					if (operation.isEmpty()) {
						break;
					}
				}
				operation.push(args[i]); // we push current op
			}

		}

		while (!operation.isEmpty()) {
			if (number.size() < 2) {
				System.err.print("I can't do ");
				System.err.print(operation.peek());
				System.err.println(" operation");
				System.exit(1);
			}
			CurrentOperation();
		}
		if (number.size() >= 2) {
			System.err.println("Wrong expression");
			System.exit(1);
		}
		System.out.println(number.pop());

	}
}
