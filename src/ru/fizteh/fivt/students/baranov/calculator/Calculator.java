package ru.fizteh.fivt.students.baranov.calculator;

import java.util.Stack;

class Calculator {

	public static Stack<String> stackOfOperations = new Stack<String>();
	public static Stack<String> stackOfNumbers = new Stack<String>();

	public static void main(String[] args) {
		for(int i = 0;  i < args.length; ++i){
			if ( args[i].matches( "[0-9 A-H a-h]*" ) ) {
				stackOfNumbers.push( args[i] );
			} else if ( args[i].equals("+") || args[i].equals("-") || args[i].equals("*") || args[i].equals("/") ) {
				if ( stackOfOperations.empty() ) {
					stackOfOperations.push( args[i] );
				} else { 
					while( !stackOfOperations.empty() && Priority( stackOfOperations.peek() ) >= Priority( args[i] ) ) {
						makeWork();
					}
					stackOfOperations.push( args[i] );
				}
			} else if ( args[i].equals("(") ) {
				stackOfOperations.push( args[i] );
			} else if ( args[i].equals(")") ) {
				while ( !stackOfOperations.empty() && !stackOfOperations.peek().equals("(") ){
					if ( stackOfOperations.empty() ) {
						System.err.println("Brackets Error");
						System.exit(1);
					}
					makeWork();
				}
				if ( stackOfOperations.empty() ) {
					System.err.println("Brackets Error");
					System.exit(1);
				}
				stackOfOperations.pop();
			}
		}
		
		while (!stackOfOperations.empty() ){
			makeWork();
		}
		
		String result = stackOfNumbers.pop();
		if ( !stackOfNumbers.empty() ) {
			System.err.println("Result Error");
			System.exit(1);
		}
		System.out.println( result );		
	}

	private static int Priority(String s) {
		if (s.equals("+") || s.equals("-")) {
			return 0;
		}
		if (s.equals("*") || s.equals("/")) {
			return 1;
		}
		if (s.equals("(") || s.equals(")")) {
			return -1;
		}
		return 0;
	}
	
	private static int doOperation(String operation, int y, int x){
		if ( operation.equals("+") ){
			return x + y;
		}
		if ( operation.equals("-") ){
			return x - y;
		}
		if ( operation.equals("*") ){
			return x * y;
		}
		if ( operation.equals("/") ){
			if ( y == 0 ) {
				System.err.println("Divide on Zero");
				System.exit(1);
			}
			return x / y;
		}
		return 0;
	}
	
	private static void makeWork(){
		String currentOperation = stackOfOperations.pop();
		if ( stackOfNumbers.empty() ) {
			System.err.println("Operands Error");
			System.exit(1);
		}
		int firstNumber = Integer.parseInt( stackOfNumbers.pop(), 18 );
		if ( stackOfNumbers.empty() ) {
			System.err.println("Operands Error");
			System.exit(1);
		}
		int secondNumber = Integer.parseInt( stackOfNumbers.pop(), 18 );
		int result = doOperation(currentOperation, firstNumber, secondNumber);
		stackOfNumbers.push( Integer.toString( result, 18 ) );
	}
}