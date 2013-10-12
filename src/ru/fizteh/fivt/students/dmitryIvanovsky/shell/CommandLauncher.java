package ru.fizteh.fivt.students.dmitryIvanovsky.shell;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;

public class CommandLauncher {

    public enum Code {EXIT, OK, ERROR}

    Object exampleClass;
    Map<String, String> commandList;

    public CommandLauncher(Object exClass, Map<String, String> comList) {
        exampleClass = exClass;
        commandList = comList;
    }

    public Code runCommand(String query) {
        query = query.trim();
        StringTokenizer token = new StringTokenizer(query);
        int countTokens = token.countTokens();
        String command = token.nextToken().toLowerCase();
        if (command.equals("exit") && countTokens == 1) {
            return Code.EXIT;
        } else if (commandList.containsKey(command)) {
            String nameMethod = commandList.get(command);
            Class[] paramTypes = new Class[] { String[].class };
            try {
                Method method = exampleClass.getClass().getMethod(nameMethod, paramTypes);
                Vector<String> commandArgs = new Vector<String>();
                for (int i=2; i<=countTokens; ++i) {
                    commandArgs.add(token.nextToken());
                }
                Object[] args = new Object[] { commandArgs.toArray(new String[commandArgs.size()]) };
                return (Code) method.invoke(exampleClass, args);
            } catch (Exception e) {
                System.err.println(String.format("Ошибка выполнения метода \'%s\'", nameMethod));
                return Code.ERROR;
            }
        } else {
            System.err.println("Неизвестная команда");
            return Code.ERROR;
        }
    }

    public Code runCommands(String query) {
        String[] command;
        command = query.split(";");
        for (String q : command) {
            Code res = runCommand(q);
            if (res != Code.OK) {
                return res;
            }
        }
        return Code.OK;
    }

    public void interactiveMode() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                Method startShellMethod = exampleClass.getClass().getMethod("startShellString", new Class[]{});
                String startShellString = (String) startShellMethod.invoke(exampleClass, new Object[]{});
                System.out.print(startShellString);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Неправильный путь");
                return;
            }
            if (sc.hasNextLine()) {
                String query = sc.nextLine();
                Code res = runCommands(query);
                if (res == Code.EXIT) {
                    return;
                }
            }
        }
    }

    public Code runShell(String[] args) {
        if (args.length > 0) {
            StringBuilder builder = new StringBuilder();
            for (String arg : args) {
                builder.append(arg);
                builder.append(' ');
            }
            String query = builder.toString();
            return runCommands(query);
        } else {
            interactiveMode();
            return Code.OK;
        }
    }

}
