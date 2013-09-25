package ru.fizteh.fivt.students.eltyshev.calc;

public class InvalidCharacterException extends RuntimeException {
    public InvalidCharacterException(String expression) {
        this.expression = expression;
    }

    public String getExpression()
    {
        return expression;
    }

    private String expression;
}
