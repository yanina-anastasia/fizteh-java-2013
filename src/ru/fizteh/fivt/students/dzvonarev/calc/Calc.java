package ru.fizteh.fivt.students.dzvonarev.calc;

import java.util.Stack;

class Calc {

    public static int priority(String str) {
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

    public static boolean noOverFlowAdd(long num1, long num2) {
        if (num2 <= 0)
	    if (Long.MIN_VALUE - num2 <= num1)
	        return true;
	    else 
		return false;
	if (num2 > 0)
	    if (Long.MAX_VALUE - num2 >= num1)
		return true;
	    else
		return false;
	return true;
    }
	
    public static boolean noOverFlowMinus(long num1, long num2) {
        if (num2 <= 0)
	    if (Long.MAX_VALUE + num2 >= num1)
	        return true;
	    else 
	        return false;
	if (num2 > 0)
	    if (Long.MIN_VALUE + num2 <= num1)
		return true;
	    else
		return false;
	return true;
    }
	
    public static boolean noOverFlowMul(long num1, long num2) {
        if (num1 == 0 || num2 == 0)
            return true;
        if (num1 > 0 && num2 > 0 || num1 < 0 && num2 < 0 || num1 < 0)
            if ((Long.MIN_VALUE / num2 <= num1) && (num1 <= Long.MAX_VALUE / num2))
	        return true;
	    else
	        return false;
        if (num2 < 0)
            if ((Long.MIN_VALUE / num2 >= num1) && (num1 >= Long.MAX_VALUE / num2))
	        return true;
	    else
	        return false;
        return true;
    }
	
    public static long doOp(long num1, String op, long num2) {
        if (op.equals("+"))
	    if (noOverFlowAdd(num1, num2))
	        return num1 + num2;
	    else {
	        System.err.println("Too big numbers plus");
		System.exit(1);
	    }
	if (op.equals("*"))
	    if (noOverFlowMul(num1, num2))
		return num1 * num2;
	    else {
	        System.err.println("Too big numbers mul");
		System.exit(1);
	    }
	if (op.equals("-"))
	    if (noOverFlowMinus(num1, num2))
	        return num1 - num2;
	    else {
		System.err.println("Too big numbers min");
		System.exit(1);
	    }
        if (op.equals("/") && num2 != 0)
	    return num1 / num2;
        else {
	    System.err.println("Division by zero");
            System.exit(1);
        }
	return 0;
    }

    private static Stack<String> number = new Stack();
    private static Stack<String> operation = new Stack();

    public static void currentOperation() {
        long num1 = Integer.parseInt(number.pop(), 19);
        long num2 = Integer.parseInt(number.pop(), 19);
        String op = operation.pop();
        long num = doOp(num2, op, num1);
        String newStr = Long.toString(num, 19);
        number.push(newStr);
    }

    public static void main(String[] args) {
        for (int i = 0; i < args.length; ++i) {
            if (args[i].matches("[0-9A-Ia-i]+")) {
                number.push(args[i]);
	    } else
	        if (!args[i].equals("+") && !args[i].equals("-")
		    && !args[i].equals("*") && !args[i].equals("/")
		        && !args[i].equals("(") && !args[i].equals(")")) {
			    System.err.println("Wrong expression");
			    System.exit(1);
		}

	    if (args[i].equals("("))
	        operation.push(args[i]);

	    if (args[i].equals(")"))
	        if (!operation.isEmpty()) {
		    while (!operation.peek().equals("(")) {
		        currentOperation();
			if (operation.isEmpty()) {
			    System.err.println("Wrong bracket sequence");
			    System.exit(1);
			}
		    }
		    operation.pop(); /* we pop open clause */
		} else {
		    System.err.println("Wrong bracket sequence");
		    System.exit(1);
		}
		

            if (args[i].equals("+") || args[i].equals("-")
		    || args[i].equals("*") || args[i].equals("/")) {
		        if (operation.isEmpty()) {
			    operation.push(args[i]);
			    continue;
			}
			if (priority(operation.peek()) < priority(args[i])) {
			    operation.push(args[i]);
			    continue;
			}
			/* now we do if op's bigger priority then now */
			while (priority(operation.peek()) >= priority(args[i])) {
			    if (number.size() < 2) {
			        System.err.print("I can't do ");
			        System.err.print(args[i]);
			        System.err.println(" operation");
			        System.exit(1);
			    }
			    currentOperation();
			    if (operation.isEmpty()) 
			        break;
                        }
		        operation.push(args[i]); /* we push current op */
            }
        }

        while (!operation.isEmpty()) {
	    if (number.size() < 2) {
	        System.err.print("I can't do ");
		System.err.print(operation.peek());
		System.err.println(" operation");
		System.exit(1);
	    }
	    currentOperation();
	}
	if (number.size() >= 2) {
	    System.err.println("Wrong expression");
	    System.exit(1);
	}
	System.out.println(number.pop());
    }
}
