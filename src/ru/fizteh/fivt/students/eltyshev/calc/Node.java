package ru.fizteh.fivt.students.eltyshev.calc;

public class Node
{
    public String Operation;
    public ExpressionType Type;
    public double Value;
    public Node Left;
    public Node Right;
}

enum ExpressionType {NUMBER, OPERATION}
