package ru.fizteh.fivt.students.nlevashov.mode;

import java.util.Vector;
import java.util.Scanner;

public class Mode {
    public static Vector<String> parse(String str, String separators) {
        String[] tokens = str.split(separators);
        Vector<String> tokensWithoutEmptyStrings = new Vector<String>();
        for (int i = 0; i < tokens.length; i++) {
            if (!tokens[i].equals("")) {
                tokensWithoutEmptyStrings.add(tokens[i]);
            }
        }
        return tokensWithoutEmptyStrings;
    }

    interface Executor {
        public boolean execute(String cmd) throws Exception;
    }

    public static void start(String[] args, Executor exec) throws Exception {
        if (args.length == 0) {
            Scanner sc = new Scanner(System.in);
            boolean flag = true;
            do {
                try {
                    System.out.print("$ ");
                    String cmdline = sc.nextLine();
                    Vector<String> commands = parse(cmdline, ";");
                    for (String s : commands) {
                        if (!exec.execute(s)) {
                            flag = false;
                            break;
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } while (flag);
        } else {
            StringBuilder sb = new StringBuilder();
            for (String s : args) {
                sb.append(s).append(' ');
            }
            String cmdline = sb.toString();
            Vector<String> commands = parse(cmdline, ";");
            for (String s : commands) {
                if (!exec.execute(s)) {
                    break;
                }
            }
        }
    }
}
