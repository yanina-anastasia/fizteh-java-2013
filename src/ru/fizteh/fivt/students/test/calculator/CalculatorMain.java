package ru.fizteh.fivt.students.test.calculator;

/**
 * @author test
 */
public class CalculatorMain 
{
    InputStream inputStream = System.in;
	PrintStream outputStream = System.out;
	private Stack <Integer> numberStack = new Stack <Integer> ();
	private Stack <Character> operandStack = new Stack <Character> ();
	private final int RADIX = 19;
	
	boolean nextIsNumber = true;
	
	int calculateExpression ( ArithmeticalExpression expression )
	{
		Scanner scanner = new Scanner ( expression.toString() );
		
		String bracket = "\\(|\\)";
		String operand = "\\+|\\-|\\*|\\/";
		String minus = "\\-";
		
		scanner.useDelimiter("((?<=\\w)(?=\\p{Punct})|(?<=\\p{Punct})(?=\\w)|(?<=\\p{Punct})(?=\\p{Punct}))");
		scanner.useRadix (RADIX);
		
		while (scanner.hasNext())
		{
			if (scanner.hasNext(bracket) )
			{
				handleBrackets (scanner.next(bracket).charAt(0));	
			}
			else if (expectedNumber())
			{
				int sign = 1;
				if (scanner.hasNext(minus))
				{
					scanner.next(minus);
					sign = -1;
				}
				if (scanner.hasNextInt())
				{
					handleNumber(scanner.nextInt() * sign);
					changeExpectation();
				}
			}
			else if (expectedOperand() && scanner.hasNext(operand) )
			{
				handleOperand(scanner.next(operand).charAt(0));
				changeExpectation();
			}
			else
			{
				scanner.next();
				outputStream.println("Error");
			}
		}
		scanner.close();

		return getResult();
	}
	
	private void handleOperand(char operand) 
	{
		outputStream.println ("Operand: "+ operand);
		while ( !operandStack.empty() && (priority(operandStack.peek()) >= priority (operand)) )
		{
			moveTheLastOperation();
		}
		operandStack.push(operand);
	}

	private int priority(char operand) 
	{
		int result = -1;
		
		if ((operand == '+') || (operand == '-'))
		{
			result = 0;
		}
		else if ((operand == '*') || (operand == '/'))
		{
			result = 1;
		}
		
		return result;
	}

	private void handleNumber(int number) 
	{
		outputStream.println ("Number: "+ number);
		numberStack.push (number);
	}

	private void handleBrackets (char operand)
	{
		if (operand == '(')
			operandStack.push(operand);
		else if (operand == ')')
		{
			//push out everything out of stack until find the '('
			while (operandStack.peek() != '(')
			{
				moveTheLastOperation();
			}
			operandStack.pop();
		}
		outputStream.println (operand);
	}
	
	private void moveTheLastOperation() 
	{
		int a = numberStack.pop();
		int b = numberStack.pop();
		
		int result = 0;
		char operand = operandStack.pop();
		switch (operand)
		{
			case '+':
			{
				result = b + a;
				break;
			}
			case '-':
			{
				result = b - a;
				break;
			}
			case '*':
			{
				result = b * a;
				break;
			}
			case '/':
			{
				result = b / a;
				break;
			}
			default:
			{
				outputStream.println ("Sign error");
			}
		}
		
		numberStack.push (result);
		
	}

	boolean expectedNumber ()
	{
		return nextIsNumber;	
	}
	
	boolean expectedOperand ()
	{
		return !nextIsNumber;
	}
	
	void changeExpectation ()
	{
		nextIsNumber = !nextIsNumber;
	}
	
	int getResult ()
	{
		return numberStack.pop();
	}

	public static void main(String[] args) 
	{
		ArithmeticalExpression expression = new ArithmeticalExpression (args[0]);
		CalculatorMain calculator = new CalculatorMain ();
		int result = calculator.calculateExpression(expression);
		calculator.outputStream.println(result);
		System.exit(1);
	}
}
