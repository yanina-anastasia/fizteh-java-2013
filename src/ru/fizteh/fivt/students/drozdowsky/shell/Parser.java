package ru.fizteh.fivt.students.drozdowsky.shell;

import java.util.ArrayList;

public class Parser {

    private static boolean in(String what, String[] where) {
        for (int i = 0; i < where.length; i++) {
            if (what.equals(where[i])) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<String[]> parse(String[] args, String[] noWSCommands) {
        args[args.length - 1] = args[args.length - 1] + ";";
        ArrayList<String[]> result = new ArrayList<String[]>();
        ArrayList<String> tempArgs = new ArrayList<String>();
        boolean noWS = false;
        for (String arg : args) {
            int last = -1;
            for (int i = 0; i < arg.length(); i++) {
                if (!noWS && arg.charAt(i) == ' ' || arg.charAt(i) == '\t') {
                    if (last + 1 != i) {
                        tempArgs.add(arg.substring(last + 1, i).trim());
                        if (tempArgs.size() == 2 && in(tempArgs.get(0), noWSCommands)) {
                            noWS = true;
                        }
                    }
                    last = i;
                }
                if (arg.charAt(i) == ';') {
                    if (last + 1 != i) {
                        tempArgs.add(arg.substring(last + 1, i).trim());
                    }
                    result.add(tempArgs.toArray(new String[tempArgs.size()]));
                    last = i;
                    tempArgs.clear();
                    noWS = false;
                }
            }
            if (last + 1 != arg.length()) {
                tempArgs.add(arg.substring(last + 1, arg.length()).trim());
            }
        }
        return result;
    }
}
