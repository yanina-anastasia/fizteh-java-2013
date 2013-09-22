package ru.fizteh.fivt.students.piakovenko.calculator;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 21.09.13
 * Time: 12:06
 * To change this template use File | Settings | File Templates.
 */
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
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < args.length; ++i) {
            s.append(args[i]);
        }
        if (s.toString() == "") {
            System.out.println("No program arguments!");
            System.exit(-1);
        }
        if (!bracketsSum (s.toString())) {
            System.out.println("Wrong brackets!");
            System.exit(-1);
        }
        Tree t = new Tree();
        Node p = t.parser(s.toString());
        if ( p.getString().equals("")) {
            System.out.println('0');
        }
        else {
            System.out.println(p.getString());
        }
    }
}
