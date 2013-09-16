package Calculator;
import java.util.Vector;
import java.util.Scanner;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;

class WrongArithmeticExpressionException extends Exception{
	public WrongArithmeticExpressionException() { 
		super(); 
	}
  	public WrongArithmeticExpressionException(String message) { 
  		super(message); 
  	}
  	public WrongArithmeticExpressionException(String message, Throwable cause) { 
  		super(message, cause); 
  	}
  	public WrongArithmeticExpressionException(Throwable cause) { 
  		super(cause); 
  	}
};

enum OperationType {
	sum("+"),
	sub("-"),
	mult("*"),
	div("/"),
	end("."),
	close(")");

	private String operationType;

	private OperationType(String type) {
		operationType = type;
	}

	public static OperationType getByType(String type) {
		
		for (OperationType operation: OperationType.values()) {
			if (operation.operationType.equals(type)) {
				return operation;
			}
		}

		throw new RuntimeException("Operation " + type + " is of unknown type");
	}

	public String getType() {
		return operationType;
	}
}

public class Calculator {
	private final static int radix = 17;
	private String curToken;
	private char curChar;
	private InputStream expression;

	Calculator() {
		curToken = "";
	}

	public static void main(String[] args) {
		String arg = "";
		int curRes;
		Calculator calc = new Calculator();

		//while (input.hasNextLine()) {
			//arg = input.nextLine();
			for (String tmp: args) {
				arg += tmp;
			}

			if (arg.equals("quit")) {
				System.exit(0);
			} else {
				calc.expression = new ByteArrayInputStream(arg.getBytes()); 
				try {
					System.out.println(calc.countExpression());
				} catch (WrongArithmeticExpressionException ex) {
					System.out.println(ex.getMessage() + ". Please try again.");
				} catch (ArithmeticException ex) {
					System.out.println("Division by zero inside the expression. Please try again.");
				}
			}
		//}
	}

	private boolean isNumber(String toCheck) {
		try {
			int tmp = Integer.parseInt(toCheck, radix);
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}

	private void parseFail(String errorMessage) throws WrongArithmeticExpressionException {
		throw new WrongArithmeticExpressionException(errorMessage);
	}

	public double countExpression() throws WrongArithmeticExpressionException {
		nextChar();
		nextToken();
		double res = parseExpression();

		if (!curToken.equals(".")) {
			throw new WrongArithmeticExpressionException(curToken + " out of place");
		}

		return res;
	}

	//In all following functions it is assumed that current token
	//is the beginning of the parsed expression

	private double parseExpression() throws WrongArithmeticExpressionException {
		//Expression is considered to be a sequence of summators,
		//connected by + and -

		double res = parseSummator();

		while ((curToken.equals("+")) || (curToken.equals("-"))) {

			OperationType operation = OperationType.getByType(curToken);

			switch(operation) {
			case sum:
				nextToken();
				
				res += parseSummator();
				
				break;
			case sub:
				nextToken();

				res -= parseSummator();
				
				break;
			case end:
			case mult:
			case div:
			case close:
				return res;
			default:
				if (operation == OperationType.end) {
					parseFail("Expression unfinished");
				} else {
					parseFail(operation.getType() + " out of place");
				}
			}
		}
		return res;
 	}

	private double parseSummator() throws WrongArithmeticExpressionException {
		//Summator is a sequence of multipliers
		//connected with * or /

		double res = parseMultiplier();

		while ((curToken.equals("*")) || (curToken.equals("/"))) {

			OperationType operation = OperationType.getByType(curToken);

			switch(operation) {
			case mult:
				nextToken();
				
				res *= parseMultiplier();
				
				break;
			case div:
				nextToken();
				
				double tmpRes = parseMultiplier();
				if (Math.abs(tmpRes) >= 1e-7) { //!= 0
					res /= tmpRes;
				} else throw new ArithmeticException();	

				break;
			case end:
			case sum:
			case sub:
			case close:
				return res;
			default:
				if (operation == OperationType.end) {
					parseFail("Expression unfinished");
				} else {
					parseFail(operation.getType() + " out of place");
				}
			}
		}
		return res;
	}

	private double parseMultiplier() throws WrongArithmeticExpressionException {
		//Multiplier is either a number or an expression in brackets

		double res = 0;

		if (curToken.equals("(")) {
			nextToken();
			
			res = parseExpression(); 

			if (!curToken.equals(")")) {
				parseFail("Unbalanced brackets");
			}

			nextToken();

		} else if (isNumber(curToken)) { 
			res = Integer.parseInt(curToken, radix);
			nextToken();

		} else if (curToken.equals("-")) {
			nextToken();
			res = -parseMultiplier();

		} else {
			if (curToken.equals(".")) {
				parseFail("Expression unfinished");
			} else {
				parseFail(curToken + " out of place");
			}
		}

		return res;
	} 

	private void nextChar() {
		try {
			if (0 < expression.available()) {
				curChar = (char)expression.read();
			}  else {
				curChar = '.';
			}
		} catch (IOException ex) {
			curChar = '.';
		}
		if (' ' == curChar) {
			nextChar();
		}
	}

	//Valid tokens:
	// (,),+,-,/,*,f, string representing a number

	private void nextToken() throws WrongArithmeticExpressionException {
		switch (curChar) {
		case '(':
		case ')':
		case '+':
		case '-':
		case '/':
		case '*':
		case '.':
			char[] curCharArray = {curChar};
			curToken = new String(curCharArray);	
			nextChar();
			break;
		default:
			if ((!Character.isDigit(curChar)) && 
				(!(radix - 10 > curChar - 'A') || !(0 <= curChar + 'A'))) {
				parseFail("Unknown symbol " + curChar); 
			}
			
			curToken = new String();

			while ((Character.isDigit(curChar)) || 
				((radix - 10 > curChar - 'A') && (0 <= curChar - 'A'))) {
				curToken += curChar;
				nextChar();
			}
		}
	}
}