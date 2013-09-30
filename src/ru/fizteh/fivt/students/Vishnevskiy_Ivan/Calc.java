package ru.fizteh.fivt.students.Vishnevskiy_Ivan;

import java.io.*;

public class Calc {

    protected static String expression; // выражение
    private static int position;        // текущая обрабатываемая позиция
    private static String token;        // последняя обработанная лексема
    private static double number;       // последнее обработанное число
    private static String tokenType;    // тип лексемы (number, operation, braces, end)

    protected static boolean isDigit(char c) {
        return (((c >= '0') && (c <= '9')) || (c == '.'));
    }

    protected static void getToken() {
        if (position == expression.length()) {
            tokenType = "end";
            return;
        }
        char c = expression.charAt(position);
        if (isDigit(c)) {
            StringBuilder numberTempString = new StringBuilder("");
            while ((position < expression.length()) && (isDigit(expression.charAt(position)))) {
                numberTempString.append(expression.charAt(position));
                ++position;
            }
            String numberString = numberTempString.toString();
            number = Double.parseDouble(numberString);
            tokenType = "number";
            return;
        }
        if ((c == '+') || (c == '-') || (c == '*') || (c == '/')) {
            tokenType = "operation";
            token = String.valueOf(c);
            ++position;
            return;
        }
        if ((c == '(') || (c == ')')) {
            tokenType = "braces";
            token = String.valueOf(c);
            ++position;
            return;
        }
    }

    protected static double prim() throws ArithmeticException, IOException {
        if (tokenType.equals("number")) {
            double tempNumber = number;
            getToken();
            return tempNumber;
        } else if (tokenType.equals("operation") && token.equals("-")) {
            getToken();
            return -prim();
        } else if (tokenType.equals("braces") && token.equals("(")) {
            getToken();
            if (tokenType.equals("end")) {
                throw new IOException("\')\' expected");
            }
            double exprValue = expr();
            if (!(tokenType.equals("braces") && token.equals(")"))) {
                throw new IOException("\')\' expected");
            }
            getToken();
            return exprValue;
        }
        throw new IOException("Primary expected");
    }

    protected static double term() throws ArithmeticException, IOException {
        double leftPrim = prim();
        while (true) {
            if (tokenType.equals("operation") && token.equals("*")) {
                getToken();
                if (tokenType.equals("end")) {
                    return leftPrim;
                }
                leftPrim *= prim();
            } else if (tokenType.equals("operation") && token.equals("/")) {
                getToken();
                if (tokenType.equals("end")) {
                    return leftPrim;
                }
                double rightPrim = prim();
                if (rightPrim != 0) {
                    leftPrim /= rightPrim;
                } else {
                    throw new ArithmeticException("Division by zero");
                }
            } else {
                return leftPrim;
            }
        }
    }

    protected static double expr() throws ArithmeticException, IOException {
        double leftTerm = term();
        while (true) {
            if (tokenType.equals("operation") && token.equals("+")) {
                getToken();
                if (tokenType.equals("end")) {
                    return leftTerm;
                }
                leftTerm += term();
            } else if (tokenType.equals("operation") && token.equals("-")) {
                getToken();
                if (tokenType.equals("end")) {
                    return leftTerm;
                }
                leftTerm -= term();
            } else {
                return leftTerm;
            }
        }
    }

    protected static double calculate() throws ArithmeticException, IOException {
        position = 0;
        getToken();
        return expr();
    }

    protected static void analyzeCharacters(StringBuilder expression) throws IOException {
        int i = 0;
        while (i < expression.length())  {
            char c = expression.charAt(i);
            if (c == ' ')
                expression.delete(i, i + 1);
            else {
                if (!isDigit(c) && (c != '(') && (c != ')') && (c != '+') && (c != '-') && (c != '*') && (c != '/'))
                    throw new IOException("Incorrect input: invalid character \'" + c + "\'");
                ++i;
            }
        }
    }

    public static void main(String[] args) {
        if ((args.length == 0) || ((args.length == 1) && (args[0].toLowerCase().equals("help")))) {
            System.out.println(" Input expression as a parameter for Calc. \n You can use both integer and real " +
                    "(with \'.\' delimiter) numbers and operations + - * /. \n Use braces () to set the priority of operations.");
            return;
        }
        StringBuilder tempExpression = new StringBuilder("");
        for (int i = 0; i < args.length; ++i) {
            tempExpression.append(args[i]);
        }
        try {
            analyzeCharacters(tempExpression);
            expression = tempExpression.toString();
            double answer = calculate();
            System.out.println("Calc: " + answer);
        } catch (IOException e) {
            System.err.println(e);
            System.out.println("Input \"help\" or start the program without parameters to get help.");
        } catch(NumberFormatException e) {
            System.err.println("Incorrect number input");
            System.out.println("Input \"help\" or start the program without parameters to get help.");
        } catch (ArithmeticException e) {
            System.err.println(e);
            System.out.println("Input \"help\" or start the program without parameters to get help.");
        }
    }
}
