package ru.fizteh.fivt.students.vyatkina.calc;

import java.io.IOException;
import java.util.Scanner;
import java.util.Stack;

public class StackCalculator {

    private Stack<Integer> numberStack = new Stack<Integer> ();
    private Stack<Character> operandStack = new Stack<Character> ();
    private final int RADIX = 19;

    boolean nextIsNumber = true;

    int calculateExpression ( String expression ) throws IOException {
        Scanner scanner = new Scanner ( "(" + expression.replace (" ", "") + ")" );

        String bracket = "\\(|\\)";
        String operand = "\\+|\\-|\\*|\\/";
        String minus = "\\-";

        scanner.useDelimiter ( "((?<=\\w)(?=\\p{Punct})|(?<=\\p{Punct})(?=\\w)|(?<=\\p{Punct})(?=\\p{Punct}))" );
        scanner.useRadix ( RADIX );

        while ( scanner.hasNext () ) {
            if ( scanner.hasNext ( bracket ) ) {
                handleBrackets ( scanner.next ( bracket ).charAt (0) );
            } else if ( expectedNumber () ) {
                int sign = 1;
                if ( scanner.hasNext ( minus ) ) {
                    scanner.next ( minus );
                    sign = -1;
                }
                if ( scanner.hasNextInt () ) {
                    handleNumber( scanner.nextInt () * sign );
                    changeExpectation ();
                }
            } else if ( expectedOperand () && scanner.hasNext ( operand ) ) {
                handleOperand ( scanner.next ( operand ).charAt (0) );
                changeExpectation ();
            } else {
                throw new IOException ( "Unexpected token: [" + scanner.next ()
                        + "]");
            }
        }
        scanner.close ();

        return getResult ();
    }

    private void handleOperand ( char operand ) throws IOException {
        while ( !operandStack.empty ()
                && ( priority ( operandStack.peek () ) >= priority ( operand ) ) ) {
            moveTheLastOperation ();
        }
        operandStack.push ( operand );
    }

    private int priority ( char operand ) {
        int result = -1;

        if ( ( operand == '+' ) || ( operand == '-' ) ) {
            result = 0;
        } else if ( ( operand == '*' ) || ( operand == '/' ) ) {
            result = 1;
        }
        return result;
    }

    private void handleNumber ( int number ) {
        numberStack.push ( number );
    }

    private void handleBrackets ( char operand ) throws IOException {
        if ( operand == '(' )
            operandStack.push ( operand );
        else if ( operand == ')' ) {
            // pop everything out of stack until find the '('
            while ( operandStack.peek () != '(' ) {
                moveTheLastOperation ();
            }
            operandStack.pop ();
        }
    }

    private void moveTheLastOperation () throws IOException {
        int a = numberStack.pop ();
        int b = numberStack.pop ();

        int result = 0;
        char operand = operandStack.pop ();
        switch ( operand ) {
            case '+' : {
                result = b + a;
                break;
            }
            case '-' : {
                result = b - a;
                break;
            }
            case '*' : {
                result = b * a;
                break;
            }
            case '/' : {
                result = b / a;
                break;
            }
            default: {
                throw new IOException("Unexpected token [" + operand + "]");
            }
        }

        numberStack.push ( result );

    }

    boolean expectedNumber () {
        return nextIsNumber;
    }

    boolean expectedOperand () {
        return !nextIsNumber;
    }

    void changeExpectation () {
        nextIsNumber = !nextIsNumber;
    }

    int getResult () {
        return numberStack.pop ();
    }

    public static void main ( String [] args ) {
        StringBuilder sb = new StringBuilder();
        for ( String s : args ) {
            sb.append(s);
        }

        StackCalculator sc = new StackCalculator ();
        try {
            int result = sc.calculateExpression ( sb.toString() );
            System.out.println ( result );
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
        System.exit (0);
    }

}
