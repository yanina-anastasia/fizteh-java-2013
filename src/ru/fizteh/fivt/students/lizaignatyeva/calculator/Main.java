package ru.fizteh.fivt.students.lizaignatyeva.calculator;

public class Main {
    public static void main(String[] args) {
        new Calculator().run(args, 17);
    }
}

class Calculator {
    private char nextChar;
    private String currentToken;
    private String expression;
    private int currentIndexInExpression = 0;
    private int base;

    private void getNextChar()
    {
        if (currentIndexInExpression < expression.length())
            nextChar = expression.charAt(currentIndexInExpression);
        currentIndexInExpression++;
    }

    private void thereIsAnError(String errorType) {
        System.err.println("Your expression is incorrect: " + errorType);
        System.exit(1);
    }

    private boolean isDigit(char character) {
        if (base != 17) {
            System.err.println("This feature is currently not available: base is not 17");
            System.exit(1);
        }
        return Character.isDigit(character) || ('A' <= character && character <= 'G') || ('a' <= character && character <= 'g');
    }

    private void getNextToken() {
        if (isDigit(nextChar))
        {
            StringBuilder number = new StringBuilder();
            while (isDigit(nextChar))
            {
                number.append(nextChar);
                getNextChar();
            }
            currentToken = number.toString();
        }
        else
        {
            currentToken = Character.toString(nextChar);
            getNextChar();
        }
        //System.out.println(currentToken);
    }

    private int readExpr()
    {
        int res = readAdd();
        while (currentToken.equals("+") || currentToken.equals("-"))
        {
            char buf = currentToken.charAt(0);
            getNextToken();
            if ((!isDigit(currentToken.charAt(0))) && (!currentToken.equals("(") && (!currentToken.equals("-"))))
                thereIsAnError("a digit expected; symbol " + Character.toString(currentToken.charAt(0)) + " found;");
            int add = readAdd();
            if (buf == '+') res += add;
            if (buf == '-') res -= add;
        }
        return res;
    }

    int readAdd()
    {
        int res = readMul();
        while (currentToken.equals("*")||currentToken.equals("/"))
        {
            char buf = currentToken.charAt(0);
            getNextToken();
            int mul = readMul();
            if (buf == '*') {
                res *= mul;
            } else {
                if (mul == 0)
                    thereIsAnError("division by zero;");
                res /= mul;
            }
        }
        return res;
    }

    int readMul()
    {
        int res;
        if (currentToken.equals("("))
        {
            getNextToken();
            res = readExpr();
            if (!currentToken.equals(")"))
                thereIsAnError("A closing bracket expected;");
            getNextToken();
        }
        else
        {
            int sign = 1;
            if (currentToken.charAt(0) == '-') {
                sign = -1;
                getNextToken();
            }
            if (!isDigit(currentToken.charAt(0)))
                thereIsAnError("A valid digit expected; ");
            res = sign*Integer.parseInt(currentToken, base);
            getNextToken();
        }
        return res;
    }


    private String cleanFromSpaces(String s) {
        s = s.replaceAll("\\s","");
        return s;
    }

    private String concatenateStrings(String[] args) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            s.append(cleanFromSpaces(args[i]));
        }
        return s.toString();
    }


    public void run(String[] args, int ourBase)
    {
        base = ourBase;
        expression = concatenateStrings(args);

       // System.out.println(expression);//for debugging

        expression += ".";
        getNextChar();
        getNextToken();

        try {
            int ans = readExpr();
            System.out.println(Integer.toString(ans, base));
        }
        catch(ArithmeticException e) {
            System.err.println("Invalid operation: " + e.getMessage());
            System.exit(1);
        }

    }
}


