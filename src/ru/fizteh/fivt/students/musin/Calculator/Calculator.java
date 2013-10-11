package ru.fizteh.fivt.students.musin.calculator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: Brother
 * Date: 03.10.13
 * Time: 23:37
 * To change this template use File | Settings | File Templates.
 */

interface Arithmetic {

    public void calculate(Stack<BigInteger> st);

}

class ReverseNotation {

    private class Operation {

        public String name;
        public int priority;
        public boolean leftAssociated;
        public Arithmetic act;

        public Operation(String n, int p, boolean la, Arithmetic a) {
            name = n;
            priority = p;
            leftAssociated = la;
            act = a;
        }

    }

    private Operation[] operations = {
            new Operation("+", 1, true, new Arithmetic() {
                @Override
                public void calculate(Stack<BigInteger> st) {
                    st.push(st.pop().add(st.pop()));
                }
            }),
            new Operation("-", 1, true, new Arithmetic() {
                @Override
                public void calculate(Stack<BigInteger> st) {
                    BigInteger second = st.pop();
                    BigInteger first = st.pop();
                    st.push(first.subtract(second));
                }
            }),
            new Operation("*", 2, true, new Arithmetic() {
                @Override
                public void calculate(Stack<BigInteger> st) {
                    st.push(st.pop().multiply(st.pop()));
                }
            }),
            new Operation("/", 2, true, new Arithmetic() {
                @Override
                public void calculate(Stack<BigInteger> st) {
                    BigInteger second = st.pop();
                    BigInteger first = st.pop();
                    st.push(first.divide(second));
                }
            })
    };

    private ArrayList<Object> line = new ArrayList<Object>();
    private Stack<BigInteger> stack = new Stack<BigInteger>();
    int base;

    public ReverseNotation(int base) {
        this.base = base;
    }

    public void addValue(String currentValue) throws Exception {
        try {
            line.add(new BigInteger(currentValue, base));
        } catch (NumberFormatException e) {
            throw new Exception("Invalid expression");
        }
    }

    public void addCommand(Stack<Operation> commands, int index) {
        if (operations[index].leftAssociated) {
            while ((commands.size() > 0) && (operations[index].priority <= commands.peek().priority)) {
                line.add(commands.pop());
            }
        }
        else {
            while ((commands.size() > 0) && (operations[index].priority < commands.peek().priority)) {
                line.add(commands.pop());
            }
        }
        commands.push(operations[index]);
    }

    public void parse(String s) throws Exception {
        line.clear();
        Stack<Operation> commands = new Stack<Operation>();
        String currentValue = "";
        byte mode = 0;
        for (char c: s.toCharArray()) {
            if (c == ' ') {
                if (currentValue.length() != 0) {
                    addValue(currentValue);
                    currentValue = "";
                    mode = 1;
                }
            }
            else if (c == '(') {
                if (mode != 0) {
                    throw new Exception("Invalid expression");
                }
                if (currentValue.length() != 0) {
                    throw new Exception("Invalid expression");
                }
                commands.push(new Operation("(", 0, false, null));
            }
            else if (c == ')') {
                if (currentValue.length() != 0) {
                    addValue(currentValue);
                    currentValue = "";
                    mode = 1;
                }
                if (mode != 1) {
                    throw new Exception("Invalid expression");
                }
                Operation command;
                do {
                    if (commands.size() == 0) {
                        throw new Exception("Invalid expression");
                    }
                    command = commands.pop();
                    if (command.name != "(") {
                        line.add(command);
                    }
                } while (command.name != "(");
            }
            else if (mode == 0) {
                if (c == '-' && currentValue.length() == 0) {
                    currentValue += c;
                    continue;
                }
                boolean operationFound = false;
                int index = 0;
                for (int i = 0; i < operations.length; i++) {
                    if (operations[i].name.charAt(0) == c) {
                        operationFound = true;
                        index = i;
                        break;
                    }
                }
                if (!operationFound) {
                    currentValue += c;
                }
                else {
                    if (currentValue.length() == 0) {
                        throw new Exception("Invalid expression");
                    }
                    addValue(currentValue);
                    currentValue = "";
                    addCommand(commands, index);
                }
            }
            else {
                boolean operationFound = false;
                for (int i = 0; i < operations.length; i++) {
                    if (operations[i].name.charAt(0) == c) {
                        operationFound = true;
                        addCommand(commands, i);
                        break;
                    }
                }
                if (!operationFound) {
                    throw new Exception("Invalid expression");
                }
                mode = 0;
            }
        }
        if (currentValue.length() != 0) {
            addValue(currentValue);
            currentValue = "";
            mode = 1;
        }
        if (mode != 1) {
            throw new Exception("Invalid expression");
        }
        while (commands.size() > 0) {
            if (commands.peek().name.equals("(")) {
                throw new Exception("Invalid expression");
            }
            line.add(commands.pop());
        }
    }

    public BigInteger Solve() throws Exception
    {
        stack.clear();
        if (line.size() == 0) {
            throw new Exception("Empty expression");
        }
        for (int i = 0; i < line.size(); i++) {
            if (line.get(i).getClass() == Operation.class) {
                ((Operation)line.get(i)).act.calculate(stack);
            }
            else {
                stack.push((BigInteger)line.get(i));
            }
        }
        return stack.pop();
    }

}


public class Calculator{

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            sb = sb.append(" ").append(args[i]);
        }
        String s = sb.toString();
        try {
            ReverseNotation tr = new ReverseNotation(18);
            tr.parse(s);
            BigInteger answer = tr.Solve();
            System.out.println(answer.toString(18));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

}
