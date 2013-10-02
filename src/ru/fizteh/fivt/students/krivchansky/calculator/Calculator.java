package ru.fizteh.fivt.students.krivchansky.calculator

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;


public class Calculator {
	private final static int radix = 19;
	private String Token;
	private char CurrentChar;
	private InputStream numericExpression;
	private boolean prevSpace; //флаг для функции, которая будет вызвана следующей
	private enum OperationType {
		plus("+"),
		minus("-"),
		mult("*"),
		div("/"),
		open("("),
		close(")"),
		end(".");
		private String opType;

		
		
		private OperationType(String type) {
			opType = type;
		}
		public static OperationType getByType(String type) throws NoSuchElementException {

			for (OperationType operation: OperationType.values()) {
				if (operation.opType.equals(type)) {
					return operation;
				}
			}
			throw new NoSuchElementException("\"" + type + "\"" + " this operation is not supported");
		}

		public String getType() {
			return opType;
		}
	}
	
	
	
	Calculator(){
		Token = "";
		prevSpace = false;
	}
	
	
	public boolean isOperation(char symbol)
	{
		for (OperationType operation: OperationType.values()) {
			if (operation.opType.equals(symbol) && symbol != '(' && symbol != ')') {
				return true;
			}
		}
		return false;
	}
	private void fail(String error) throws unknownExpressionException{
		throw new unknownExpressionException(error); 
	}
	private void getChar() throws unknownExpressionException{
		try{
			if(numericExpression.available() > 0){
			CurrentChar = (char) numericExpression.read();
		}
		else CurrentChar = '.';
		} catch (IOException except){ 
			CurrentChar = '.';
		}
		if (CurrentChar == ' '){
			if (!prevSpace){
				prevSpace = true;
			    getChar();
			}
			else{
				fail("Two spaces in a row. Not supported numeric expression");
			}
		}
	}
    private void getToken() throws unknownExpressionException{
    	prevSpace = false;
    	/*if (CurrentChar == '-' || CurrentChar == '+' ||
    		CurrentChar == '*' || CurrentChar == '/'){
    		if (isOperation(Token.charAt(Token.length() - 1))){
    			fail("Two operations in a row");
    		}
    	}*/
    	if (CurrentChar == '(' || CurrentChar == ')' || CurrentChar == '-' || 
    		CurrentChar == '+' || CurrentChar == '*' || CurrentChar == '/' || CurrentChar == '.'){
    		char[] tempArr = {CurrentChar};
    		Token = new String(tempArr);
    		getChar();
    	}
    	else	{
    		if( !valid() ){
    			fail(CurrentChar + " is unknown");
    		}
    		StringBuilder tokenBuild = new StringBuilder();
    		while( (valid()) && (!prevSpace) ){
    			tokenBuild.append(CurrentChar);
    			getChar();
    		}
    		prevSpace = false;
    		Token = tokenBuild.toString();
    	}	
    	
    }
    public boolean valid(){
    	if (CurrentChar >= '0' && CurrentChar <= '9' || 'A' <= CurrentChar && 'A' + radix - 10 >= CurrentChar){
    		return true;
    	}
    	return false;
    }
    public int doEverything() throws unknownExpressionException, ArithmeticException, NoSuchElementException{
    	int result;
    	getChar();
    	getToken();
    	result = parseNoPriority();
    	if (!(Token.charAt(Token.length() - 1) == '.')){
    		fail("Too long expression. Unable to work it out");
    	}
    	return result;
    }    
    private boolean Numeral(String smth){
    	try {
    		int tmp = Integer.parseInt(smth, radix);
    	} catch (NumberFormatException op) {
    		return false;
    	}
    	return true;
    }
    
    
    
    
    
    private int parseNoPriority() throws unknownExpressionException, ArithmeticException, NoSuchElementException {
    	int result = parseHighPriority();
    	int partialResult;
    	OperationType last = OperationType.getByType(Token);
    	while (last.opType == "-" || last.opType == "+"){
    		if(last.opType == "-" || last.opType == "+"){
    			if (last.opType == "+"){
    				getToken();
    				partialResult = parseHighPriority();
    				if (Integer.MAX_VALUE - Math.abs(partialResult) < Math.abs(result)){
    					fail("Overflow");
    				}
    				result += partialResult;
    				return result;
    			} else {
    				getToken();
    				partialResult = parseHighPriority();
    				if (Integer.MAX_VALUE - Math.abs(partialResult) < Math.abs(result)){
    					fail("Overflow");
    				}
    				result -= partialResult;
    				return result;
    			}
    		}
    	    if (last.opType == "*" || last.opType == "/" || last.opType == ")" || last.opType == "."){
    		    return result;
    	    }
    	    else {
    		    if (last.opType == "."){
    			    fail("Unexpected end of expression");
    		    } else {
    			    fail(last.getType() + " bad placement");
    		    }
    	    }
    	}
    	return result;
    }
    private int parseHighPriority() throws unknownExpressionException, ArithmeticException, NoSuchElementException{
    	int result = parseHighestPriority();
    	int partialResult;
    	OperationType last = OperationType.getByType(Token);
    	while (last.opType == "*" || last.opType == "/") {
    		if (last.opType == "*" || last.opType == "/"){
    			if (last.opType == "*") {
    				getToken();
    				partialResult = parseHighestPriority();
    				if (partialResult!=0 && Integer.MAX_VALUE / Math.abs(partialResult) < Math.abs(result)){
    					fail("Overflow");
    				}
    				result *= partialResult;
    				return result;
    			} else {
    				getToken();
    				partialResult = parseHighestPriority();
    				if (0 != partialResult){
    					result /= partialResult;
    					return result;
    				} else throw new ArithmeticException("Division by zero");
    			}
    		}
    	    if (last.opType == "-" || last.opType == "+" || last.opType == ")" || last.opType == "."){
    		    return result;
    	    }
    	    else {
    		    if (last.opType == "."){
    	                fail("Unexpected end of expression");
    	            } else {
    		    	    fail(last.getType() + " bad placement");
    	    	    }
    		    }
    	    }
    	return result;
    }
    private int parseHighestPriority() throws unknownExpressionException, ArithmeticException{
    	int result = 0;
    	if (Token.equals("(")){
    		getToken();
    		result = parseNoPriority();
    		if (!Token.equals(")")){
    			fail("Broken bracket balance");
    		}
    		getToken();
    	} else if(Numeral(Token)){
    		result = Integer.parseInt(Token, radix);
    		getToken();
    	} else if(Token.equals("-")){
    		getToken();
    		result = -parseHighestPriority();		
    	} else {
    		if(Token.equals(".")){
    			fail("Unexpected end of expression");
    		} else {
    			fail(Token + " bad placement"); 
    		}
    	}
    	return result;
    }
    
    public static void main(String[] args){
    	String input;
    	Integer Result;
    	Calculator worker = new Calculator();
    	StringBuilder inputBuild = new StringBuilder();
    	for (String t : args){
    		inputBuild.append(t + " ");
    	}
    	input = inputBuild.toString();
    	if (input.equals("")){
    		System.out.println("No way! Something must be entered!");
    		System.exit(1);
    	}
    	worker.numericExpression = 	new ByteArrayInputStream(input.getBytes());
    	try{
    		Result = worker.doEverything();
    		System.out.println(Integer.toString(Result, radix).toUpperCase());
    	} catch (unknownExpressionException except) {
    		System.out.println(except.getMessage() + " Me not understand you.");
    		System.exit(2);
    	}
    	catch (ArithmeticException except) {
    		System.out.println(except.getMessage() + "I can't divide by zero.");
    		System.exit(3);
    	}
    	catch (NoSuchElementException except) {
    		System.out.println(except.getMessage() + " Me not understand you.");
    		System.exit(4);
    	}
    	System.exit(0);
    }
}


