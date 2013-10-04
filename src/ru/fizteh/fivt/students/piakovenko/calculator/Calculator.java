package ru.fizteh.fivt.students.piakovenko.calculator;

import java.io.IOException;

public class Calculator {

    private static boolean bracketsSum(String s) {
        int count = 0;
        for (int i = 0; i < s.length(); ++i) {
            if (!(s.charAt(i) == '+' || s.charAt(i) == '-'|| s.charAt(i) == '*' || s.charAt(i) == '/')) {
                if (!(('a' <= s.charAt(i) && s.charAt(i) <= 'i') || ('A' <= s.charAt(i) && s.charAt(i) <= 'I'))) {
                    if (!(s.charAt(i) == ' ' || s.charAt(i) == '(' || s.charAt(i) == ')' || (s.charAt(i) >= '0' && s.charAt(i) <= '9'))) {
                        System.err.println("Error! Forbidden symbol: " + s.charAt(i));
                        System.exit(5);
                    }
                }
            }
            if (s.charAt(i) == '('){
                ++count;
            }
            else if (s.charAt(i) == ')') {
                --count;
            }
        }
        return (count == 0);
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Error: No program arguments!");
            System.exit(1);
        }
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < args.length; ++i) {
            s.append(' ' + args[i]);
        }
        if (!bracketsSum (s.toString())) {
            System.err.println("Error: Wrong brackets!");
            System.exit(2);
        }
        Node p = null;
        try{
            Tree t = new Tree();
            p = t.parseCalculationTree(s.toString());
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(3);
        }
        if ( p.getString().isEmpty()) {
           System.out.println('0');
        }
        else {
           System.out.println(p.getString());
        }
    }
}
