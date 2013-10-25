package ru.fizteh.fivt.students.drozdowsky.Modes;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Utils {
    public static String[] scanArgs(Scanner in) {

        System.out.print("$ ");
        String temp;
        if (!in.hasNextLine()) {
            System.exit(0);
        }
        temp = in.nextLine();
        if (!temp.isEmpty()) {
            return new String[]{temp};
        } else {
            return new String[0];
        }
    }

    private static boolean in(String what, String[] where) {
        for (String wh : where) {
            if (what.equals(wh)) {
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

    public static HashMap<String, Method> getMethods(String[] commandNames, Class commandClass, Class controllerClass) {
        HashMap<String, Method> map = new HashMap<String, Method>();
        try {
            for (String commandName: commandNames) {
                map.put(commandName, commandClass.getMethod(commandName, controllerClass, String[].class));
            }
        } catch (NoSuchMethodException e) {
            System.err.println(e.getMessage());
        }
        return map;
    }

    public static HashMap<String, Method> getMethods(String[] commandNames, Class commandClass) {
        HashMap<String, Method> map = new HashMap<String, Method>();
        try {
            for (String commandName: commandNames) {
                map.put(commandName, commandClass.getMethod(commandName, String[].class));
            }
        } catch (NoSuchMethodException e) {
            System.err.println(e.getMessage());
        }
        return map;
    }
}
