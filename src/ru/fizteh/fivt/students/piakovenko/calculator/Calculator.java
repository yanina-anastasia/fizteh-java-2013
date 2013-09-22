package ru.fizteh.fivt.students.piakovenko.calculator;

public class Calculator {

    private static boolean bracketsSum(String s) {
        int count = 0;
        for (int i = 0; i < s.length(); ++i) {
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
            System.out.println("Error: No program arguments!");
            return;
        }
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < args.length; ++i) {
            s.append(args[i]);
        }
        if (!bracketsSum (s.toString())) {
            System.out.println("Error: Wrong brackets!");
            return;
        }
        Node p;
        try{
            Tree t = new Tree();
            p = t.parser(s.toString());
        } catch ( RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }
        if ( p.getString().equals("")) {
           System.out.println('0');
        }
        else {
           System.out.println(p.getString());
        }
    }
}
