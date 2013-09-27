import java.math.BigInteger;
import java.util.EmptyStackException;
import java.util.ArrayList;
import java.util.Stack;

class DivisionByZeroException extends Exception {
    DivisionByZeroException(String message) {
        super(message);
    }
}

class InappropriateSymbolException extends Exception {
    InappropriateSymbolException(String message) {
        super(message);
    }
}

class InvalidLexemMetException extends Exception {
    InvalidLexemMetException(String message) {
        super(message);
    }
}

class Calculator {
    private enum Lexem {NUMBER, ADDITION, SUBTRACTION, MULTIPLICATION, DIVISION, OBRACKET, CBRACKET};
    private enum State {START, NUMBER, ADDITION, SUBTRACTION, MULTIPLICATION, DIVISION, OBRACKET, CBRACKET};
    private String expression;
    private ArrayList<Pair<Lexem, Integer>> lexems = new ArrayList<Pair<Lexem, Integer>>();

    Calculator(String expression) throws InappropriateSymbolException {
        this.expression = expression;
        int pointer = 0;
        lexems.add(new Pair<Lexem, Integer>(Lexem.OBRACKET, -1));
        while(pointer < expression.length()) {
            char currentSymbol = expression.charAt(pointer);
            if(currentSymbol == '+') //Can not use switch here because I need range of values to determine if it is a number.
                lexems.add(new Pair<Lexem, Integer>(Lexem.ADDITION, pointer));
            else if(currentSymbol == '-')
                lexems.add(new Pair<Lexem, Integer>(Lexem.SUBTRACTION, pointer));
            else if(currentSymbol == '*')
                lexems.add(new Pair<Lexem, Integer>(Lexem.MULTIPLICATION, pointer));
            else if(currentSymbol == '/')
                lexems.add(new Pair<Lexem, Integer>(Lexem.DIVISION, pointer));
            else if(currentSymbol == '(')
                lexems.add(new Pair<Lexem, Integer>(Lexem.OBRACKET, pointer));
            else if(currentSymbol == ')')
                lexems.add(new Pair<Lexem, Integer>(Lexem.CBRACKET, pointer));
            else if('0' <= currentSymbol && currentSymbol <= '9' || 'A' <= currentSymbol && currentSymbol <= Constants.LASTLETTER) {
                do {
                    pointer++;
                    if(pointer < expression.length())
                        currentSymbol = expression.charAt(pointer);
                    else
                        break;
                } while('0' <= currentSymbol && currentSymbol <= '9' || 'A' <= currentSymbol && currentSymbol <= Constants.LASTLETTER);
                pointer--;
                lexems.add(new Pair<Lexem, Integer>(Lexem.NUMBER, pointer));
            } else
                throw new InappropriateSymbolException(String.format("Inappropriate symbol '%c' found.", currentSymbol));
            pointer++;
        }
        lexems.add(new Pair<Lexem, Integer>(Lexem.CBRACKET, expression.length()));
    }

    private Fraction getNumber(int lexemNumber) {
        BigInteger result;
        try {
            result = new BigInteger(this.expression.substring(lexems.get(lexemNumber - 1).getRight() + 1, lexems.get(lexemNumber).getRight() + 1), Constants.RADIX);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(String.format("Lexem #%d can not be parsed as a number.", lexemNumber - 1));
        }
        return new Fraction(result);
    }

    private String getInvalidLexemMetExceptionText(int lexemNumber) {
        return String.format("Lexem #%d is invalid.", lexemNumber - 1);
    }

    private void performOperation(Stack<Fraction> numbers, Lexem lexem) throws DivisionByZeroException {
        Fraction a = numbers.pop(), b = numbers.pop(), result; // Stack is not empty because we use automata, which garantees us correct syntax.
        switch(lexem) {
            case ADDITION:
                result = b.add(a);
                break;
            case SUBTRACTION:
                result =  b.subtract(a);
                break;
            case MULTIPLICATION:
                result =  b.multiply(a);
                break;
            case DIVISION:
                if(a.isZero()) throw new DivisionByZeroException("Division by zero occured.");
                else
                    result =  b.divide(a);
                break;
            default:
                throw new RuntimeException(Constants.BUG);
        }
        numbers.push(result);
    }

    private void popBracketBlock(Stack<Fraction> numbers, Stack<Lexem> operations) throws InvalidLexemMetException, DivisionByZeroException {
        try {
            Lexem currentLexem;
            while((currentLexem = operations.pop()) != Lexem.OBRACKET) {
                performOperation(numbers, currentLexem);
            }
        } catch (EmptyStackException e) {
            throw new InvalidLexemMetException("Bracket sequence is not balanced.");
        }
    }

    private int getOperationPriority(Lexem operation) {
        switch(operation) {
            case ADDITION:
            case SUBTRACTION:
                return 0;
            case MULTIPLICATION:
            case DIVISION:
                return 1;
            case OBRACKET:
                return -1;
            default:
                throw new RuntimeException(Constants.BUG);
        }
    }

    private void pushOperation(Stack<Fraction> numbers, Stack<Lexem> operations, Lexem operation) throws DivisionByZeroException {
        while(!operations.empty() && getOperationPriority(operations.peek()) > getOperationPriority(operation))
            performOperation(numbers, operations.pop());
        operations.push(operation);
    }

    private State operationToState(Lexem operation) {
        switch(operation) {
            case ADDITION: return State.ADDITION;
            case SUBTRACTION: return State.SUBTRACTION;
            case MULTIPLICATION: return State.MULTIPLICATION;
            case DIVISION: return State.DIVISION;
            default:
                throw new RuntimeException(Constants.BUG);
        }
    }

    public Fraction calculate() throws InvalidLexemMetException, NumberFormatException, DivisionByZeroException {
        State state = State.START;
        Stack<Lexem> operations = new Stack<Lexem>();
        Stack<Fraction> numbers = new Stack<Fraction>();
        for(int i = 0; i < lexems.size(); i++) {
            Lexem lexemType = lexems.get(i).getLeft();
            switch(state) { //If we identify Lexem with State this code might be shorter; I don't like to use dirty tricks on Java tho.
                case START:
                    switch(lexemType) {
                        case OBRACKET:
                            operations.push(Lexem.OBRACKET);
                            state = State.OBRACKET;
                            break;
                        default:
                            throw new InvalidLexemMetException(getInvalidLexemMetExceptionText(i));
                    }
                break;
                case OBRACKET:
                    switch (lexemType) {
                        case NUMBER:
                            numbers.push(getNumber(i));
                            state = State.NUMBER;
                            break;
                        case OBRACKET:
                            operations.push(Lexem.OBRACKET);
                            state = State.OBRACKET;
                            break;
                        default:
                            throw new InvalidLexemMetException(getInvalidLexemMetExceptionText(i));
                    }
                break;
                case NUMBER:
                    switch (lexemType) {
                        case ADDITION:
                        case SUBTRACTION:
                        case MULTIPLICATION:
                        case DIVISION:
                            pushOperation(numbers, operations, lexemType);
                            state = operationToState(lexemType);
                            break;
                        case CBRACKET:
                            popBracketBlock(numbers, operations);
                            state = State.CBRACKET;
                            break;
                        default:
                            throw new InvalidLexemMetException(getInvalidLexemMetExceptionText(i));
                    }
                break;
                case ADDITION:
                case SUBTRACTION:
                case MULTIPLICATION:
                case DIVISION:
                    switch(lexemType) {
                        case NUMBER:
                            numbers.push(getNumber(i));
                            state = State.NUMBER;
                            break;
                        case OBRACKET:
                            operations.push(Lexem.OBRACKET);
                            state = State.OBRACKET;
                            break;
                        default:
                            throw new InvalidLexemMetException(getInvalidLexemMetExceptionText(i));
                    }
                break;
                case CBRACKET:
                    switch (lexemType) {
                        case ADDITION:
                        case SUBTRACTION:
                        case MULTIPLICATION:
                        case DIVISION:
                            pushOperation(numbers, operations, lexemType);
                            state = operationToState(lexemType);
                            break;
                        case CBRACKET:
                            popBracketBlock(numbers, operations);
                            state = State.CBRACKET;
                            break;
                        default:
                            throw new InvalidLexemMetException(getInvalidLexemMetExceptionText(i));
                    }
            }
        }
        if(!operations.empty())
            throw new InvalidLexemMetException("Expression is incorrect.");
        if(numbers.size() != 1) //It should never happen as far as I understand.
            throw new RuntimeException(Constants.BUG);
        return numbers.pop();
    }
}
