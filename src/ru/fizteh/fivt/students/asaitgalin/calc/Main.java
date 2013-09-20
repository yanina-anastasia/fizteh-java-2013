package ru.fizteh.fivt.students.asaitgalin.calc;

import java.io.PrintStream;

public class Main {
    public static void main(String[] args) {
        PrintStream ps = System.out;
        StringBuilder input = new StringBuilder();
        for (String s: args) {
            input.append(s);
        }
        if (args.length == 0) {
            ps.println("No arguments\nUsage example: calc \"2+2*2\"");
            System.exit(0);
        }
        Lexer lexer = new Lexer(input.toString());
        Parser parser = new Parser(lexer);
        try {
            ps.println(Integer.toString(parser.parseExpr(), 19).toUpperCase());
        } catch (IllegalArgumentException iae) {
            ps.println(iae.getMessage());
        }
    }
}
