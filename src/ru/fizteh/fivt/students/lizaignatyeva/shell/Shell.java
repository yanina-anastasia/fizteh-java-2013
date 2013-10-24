package ru.fizteh.fivt.students.lizaignatyeva.shell;

import java.util.Hashtable;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.nio.*;
import java.io.File;
import java.util.Arrays;
import ru.fizteh.fivt.students.lizaignatyeva.shell.CommandFactory;

public class Shell {
    static Hashtable<String, Command> commandsMap = new Hashtable<String, Command>();

    public static File path;
    public static CommandFactory factory;

    static void setPath(File newPath) {
        path = newPath;
    }

    static File getPath() {
        return path;
    }

    public static String getFullPath(String smallPath) {
        File myFile = new File(smallPath);
        if (myFile.isAbsolute()) {
            return smallPath;
        } else {
            return path.getAbsolutePath() + File.separator + smallPath;
        }
    }
    public static String concatenateWithDelimiter(String[] strings, String delimiter) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            builder.append(strings[i]);
            if (i != strings.length - 1) {
                builder.append(delimiter);
            }
        }
        return builder.toString();
    }

    public static void addCommands() {
        commandsMap.put("cd", new CdCommand());
        commandsMap.put("mkdir", new MkdirCommand());
        commandsMap.put("pwd", new PwdCommand());
        commandsMap.put("rm", new RmCommand());
        commandsMap.put("cp", new CpCommand());
        commandsMap.put("mv", new MvCommand());
        commandsMap.put("dir", new DirCommand());
        commandsMap.put("exit", new ExitCommand());
    }

    public static void main(String[] args) {
        path = new File(".");
        addCommands();
        factory = new CommandFactory(commandsMap);
        if (args.length != 0) {
            String commands = concatenateWithDelimiter(args, " ");
            runCommands(commands);
        } else {
            Scanner input = new Scanner(System.in);
            while (true) {
                try {
                    System.out.print(String.format("%s$ ", path.getCanonicalPath()));
                } catch (Exception e) {
                    System.err.println("Something went wrong!");
                    return;
                }
                String commands = input.nextLine();
                if (commands.length() != 0) {
                    runCommands(commands);
                }
            }
        }

    }

    public static void runCommands(String commands) {
        String[] commandsList = commands.split(";");
        for (String commandWithArguments: commandsList) {
            String[] tokens = tokenizeCommand(commandWithArguments);
            if (tokens.length == 0) {
                continue;
            }
            Command command;
            try {
                command = factory.makeCommand(tokens[0]);
            } catch (Exception e){
                System.err.println(e.getMessage());
                return;
            }
            try {
                command.run(Arrays.copyOfRange(tokens, 1, tokens.length));
            } catch (Exception e) {
                System.err.println(command.name + ": " + e.getMessage());
                return;
            }
        }
    }

    public static String[] tokenizeCommand(String s) {
        s = s.trim();
        StringTokenizer tokenizer = new StringTokenizer(s);
        String[] result = new String[tokenizer.countTokens()];
        for (int i = 0; tokenizer.hasMoreTokens(); ++i) {
            result[i] = tokenizer.nextToken();
        }
        return result;
    }
}
