package ru.fizteh.fivt.students.dmitryIvanovsky.shell;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Scanner;

public class CommandLauncher {

    public enum Code {
        EXIT,
        OK,
        ERROR
    }

    CommandAbstract exampleClass;
    Map<String, String> commandList;
    Map<String, Method> commandMethod;

    public CommandLauncher(CommandAbstract exampleClass, Map<String, String> commandList) throws NoSuchMethodException {
        this.exampleClass = exampleClass;
        this.commandList = commandList;
        commandMethod = new HashMap<String, Method>();
        Class[] paramTypes = new Class[]{String[].class};
        for (String key : commandList.keySet()) {
            commandMethod.put(key, exampleClass.getClass().getMethod(key, paramTypes));
        }
    }

    public Code runCommand(String query, boolean isInteractiveMode) {
        query = query.trim();
        StringTokenizer token = new StringTokenizer(query);
        int countTokens = token.countTokens();

        if (countTokens > 0) {
            String command = token.nextToken().toLowerCase();
            if (command.equals("exit") && countTokens == 1) {
                return Code.EXIT;
            } else if (commandList.containsKey(command)) {
                Method method = commandMethod.get(command);
                Vector<String> commandArgs = new Vector<>();
                for (int i = 2; i <= countTokens; ++i) {
                    commandArgs.add(token.nextToken());
                }
                try {
                    if (exampleClass.selfParsing()) {
                        Object[] args = new Object[]{new String[]{query}};
                        return (Code) method.invoke(exampleClass, args);
                    } else {
                        Object[] args = new Object[]{commandArgs.toArray(new String[commandArgs.size()])};
                        return (Code) method.invoke(exampleClass, args);
                    }
                } catch (Exception e) {
                    System.err.println(String.format("Ошибка выполнения команды \'%s\'", command));
                    return Code.ERROR;
                }
            } else {
                System.err.println("Неизвестная команда");
                return Code.ERROR;
            }
        } else {
            if (!isInteractiveMode) {
                System.err.println("Пустой ввод");
            }
            return Code.ERROR;
        }
    }

    public Code runCommands(String query, boolean isInteractiveMode) {
        String[] command;
        command = query.split(";");
        for (String q : command) {
            Code res = runCommand(q, isInteractiveMode);
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
                System.out.print(exampleClass.startShellString());
            } catch (Exception e) {
                //e.printStackTrace();
                System.err.println("Неправильный путь");
                return;
            }
            if (sc.hasNextLine()) {
                String query = sc.nextLine();
                if (query.length() == 0) {
                    continue;
                }
                Code res = runCommands(query, true);
                if (res == Code.EXIT) {
                    return;
                }
            } else {
                return;
            }
        }
    }

    public Code runShell(String[] args) throws IOException {
        if (args.length > 0) {
            StringBuilder builder = new StringBuilder();
            for (String arg : args) {
                builder.append(arg);
                builder.append(' ');
            }
            String query = builder.toString();
            exampleClass.exit();
            return runCommands(query, false);
        } else {
            interactiveMode();
            exampleClass.exit();
            return Code.OK;
        }
    }

}
