package ru.fizteh.fivt.students.mikhaylova_daria.db;

import java.util.Scanner;
import java.lang.reflect.Method;

public class Parser {
    public static void parser(String[] arg, Class workingClass) throws Exception{
        boolean flag = true;
        Scanner input = new Scanner(System.in);
        String[] commandString;
        String inputString;
        while (flag) {
            if (arg.length == 0) {
                System.out.print("$ ");
                if (input.hasNextLine()) {
                    inputString = input.nextLine();
                    commandString = inputString.split("[;]");
                    manager(commandString, false, workingClass);
                } else {
                    System.exit(0);
                }
            } else {
                pack(arg, workingClass);
                flag = false;
            }
        }
    }

    private static void manager(String[] commandString, boolean pack, Class workingClass) throws Exception {
        int i;
        Object obj;
        obj = workingClass.newInstance();
        Class[] parametrTypes = new Class[] {String[].class};
        for (i = 0; i < commandString.length; ++i) {
            String[] command = commandString[i].trim().split("\\s+");
            Method currentMethod = workingClass.getMethod(command[0], parametrTypes);
            currentMethod.invoke(command);
        }
    }

    private static void pack(String[] arg, Class workingClass) throws Exception {
        StringBuilder builderArg;
        builderArg = new StringBuilder();
        int i;
        for (i = 0; i < arg.length; ++i) {
            builderArg.append(arg[i]);
            builderArg.append(" ");
        }
        String[] command = builderArg.toString().split("[;]");
        manager(command, true, workingClass);

    }

}
