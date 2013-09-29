package ru.fizteh.fivt.students.ADanilyak.calculator;

/**
 * User: Alexander
 * Date: 29.09.13
 * Time: 12:34
 */

import java.util.*;
import java.util.regex.Pattern;

class Calculator {

    private String equation;

    public Calculator(String str) {
        equation = str;
    }

    // Бьем строку в лист токенов
    public ArrayList<String> stringIntoTokens() {
        ArrayList<String> tokens = new ArrayList<String>();
        Scanner tokenize = new Scanner(this.equation);
        String currentToken = "";
        while (tokenize.hasNext()) {
            if (!tokens.isEmpty() && currentToken.matches("-[0-9A-Z]+|[0-9A-Z]+|\\)|\\s+")) {
                tokens.add(currentToken = tokenize.findInLine(Pattern.compile("[0-9A-Z]+|-|\\+|\\*|/|\\(|\\)|\\s+")));
            } else {
                tokens.add(currentToken = tokenize.findInLine(Pattern.compile("-[0-9A-Z]+|[0-9A-Z]+|\\+|\\*|/|\\(|\\)|\\s+")));
            }
        }
        return tokens;
    }

    // Переводим лист токенов в обратную польскую нотацию
    public ArrayDeque<String> intoReversePolishNotation(ArrayList<String> tokens) {
        boolean lastTokenWasNumber = false;
        ArrayDeque<String> reversePolishNotation = new ArrayDeque<String>();
        Stack<String> operators = new Stack<String>();
        String tempToken;
        while (!tokens.isEmpty()) {
            tempToken = tokens.get(0);
            tokens.remove(0);

            if (tempToken.matches("-[0-9A-Z]+|[0-9A-Z]+")) {
                if (lastTokenWasNumber) {
                    System.err.print("wrong equation");
                    System.exit(1);
                }
                reversePolishNotation.add(tempToken);
                lastTokenWasNumber = true;
            } else if (tempToken.matches("\\s+")) {
                continue;
            } else {
                RequestType request = RequestType.getType(tempToken);
                switch (request) {
                    case PLUS:
                        while (!operators.empty()) {
                            if (!operators.peek().equals("(")) {
                                reversePolishNotation.add(operators.pop());
                            } else {
                                break;
                            }
                        }
                        operators.push("+");
                        lastTokenWasNumber = false;
                        break;
                    case MINUS:
                        while (!operators.empty()) {
                            if (!operators.peek().equals("(")) {
                                reversePolishNotation.add(operators.pop());
                            } else {
                                break;
                            }
                        }
                        operators.push("-");
                        lastTokenWasNumber = false;
                        break;
                    case MULT:
                        while (!operators.empty()) {
                            if (!operators.peek().equals("(") && !operators.peek().equals("+") &&
                                    !operators.peek().equals("-")) {
                                reversePolishNotation.add(operators.pop());
                            } else {
                                break;
                            }
                        }
                        operators.push("*");
                        lastTokenWasNumber = false;
                        break;
                    case DIV:
                        while (!operators.empty()) {
                            if (!operators.peek().equals("(") && !operators.peek().equals("+") &&
                                    !operators.peek().equals("-")) {
                                reversePolishNotation.add(operators.pop());
                            } else {
                                break;
                            }
                        }
                        operators.push("/");
                        lastTokenWasNumber = false;
                        break;
                    case OBRCKT:
                        operators.push("(");
                        break;
                    case CBRCKT:
                        Boolean correctEquation = false;
                        while (!operators.empty()) {
                            if (!operators.peek().equals("(")) {
                                reversePolishNotation.add(operators.pop());
                            } else {
                                correctEquation = true;
                                break;
                            }
                        }
                        if (!correctEquation) {
                            System.err.print("wrong equation");
                            System.exit(1);
                        }

                        operators.pop();
                        break;
                }
            }
        }

        while (!operators.empty()) {
            if (operators.peek().equals("(")) {
                System.err.print("wrong equation");
                System.exit(1);
            }
            reversePolishNotation.add(operators.pop());
        }

        return reversePolishNotation;
    }

    // Проводим вычисления
    public String calculate(Integer radix) {
        ArrayDeque<String> reversePolishNotation = this.intoReversePolishNotation(this.stringIntoTokens());
        String tempToken;
        Integer firstTerm, secondTerm;
        Stack<Integer> forCalculation = new Stack<Integer>();

        while (!reversePolishNotation.isEmpty()) {
            tempToken = reversePolishNotation.getFirst();
            reversePolishNotation.remove();

            if (tempToken.matches("-[0-9A-Z]+|[0-9A-Z]+")) {
                try {
                    forCalculation.add(Integer.parseInt(tempToken, radix));
                } catch (NumberFormatException ex) {
                    System.err.print("wrong equation");
                    System.exit(1);
                }
            } else {
                RequestType request = RequestType.getType(tempToken);
                switch (request) {
                    case PLUS:
                        secondTerm = forCalculation.pop();
                        firstTerm = forCalculation.pop();
                        forCalculation.push(SafeOperations.safeAdd(firstTerm, secondTerm));
                        break;
                    case MINUS:
                        secondTerm = forCalculation.pop();
                        firstTerm = forCalculation.pop();
                        forCalculation.push(SafeOperations.safeSubtract(firstTerm, secondTerm));
                        break;
                    case MULT:
                        secondTerm = forCalculation.pop();
                        firstTerm = forCalculation.pop();
                        forCalculation.push(SafeOperations.safeMultiply(firstTerm, secondTerm));
                        break;
                    case DIV:
                        secondTerm = forCalculation.pop();
                        firstTerm = forCalculation.pop();
                        forCalculation.push(firstTerm / secondTerm);
                        break;
                }
            }
        }
        return Integer.toString(forCalculation.peek(), radix);
    }
}
