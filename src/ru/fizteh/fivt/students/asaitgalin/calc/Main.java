package ru.fizteh.fivt.students.asaitgalin.calc;

public class Main {
    public static void main(String[] args) {
        StringBuilder input = new StringBuilder();
        for (String s: args) {
            input.append(s);
        }
        if (args.length == 0) {
            System.out.println("No arguments\nUsage example: calc \"2+2*2\"");
            System.exit(0);
        }
        Lexer lexer = new Lexer(input.toString());
        try {
            Parser parser = new Parser(lexer);
            System.out.println(Integer.toString(parser.parseExpr(), 19).toUpperCase());
        } catch (IllegalExpressionException iee) {
            System.err.println(iee.getMessage());
        }
    }
}
