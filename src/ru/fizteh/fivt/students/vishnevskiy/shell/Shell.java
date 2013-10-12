package ru.fizteh.fivt.students.vishnevskiy.shell;

import java.util.Scanner;

public class Shell {

    private static int executeLine(String line, CommandsOperator commandsOperator) {
        String[] commands = line.split(";");
        for (String command : commands) {
            int status = commandsOperator.runCommand(command);
            if (status == 1) {
                System.err.flush();
                return 1;
            }
        }
        return 0;
    }

    private static void interactiveMode(CommandsOperator commandsOperator) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("$ ");
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            executeLine(line, commandsOperator);
            System.out.print("$ ");
        }
    }

    private static void batchMode(String[] args, CommandsOperator commandsOperator) {
        StringBuilder lineBuilder = new StringBuilder();
        for (int i = 0; i < args.length; ++i) {
            lineBuilder.append(args[i]);
            lineBuilder.append(' ');
        }
        String line = lineBuilder.toString();
        int status = executeLine(line, commandsOperator);
        System.exit(status);
    }

    public static void main(String[] args) {
        CommandsOperator commandsOperator = new CommandsOperator();
        if (args.length == 0) {
            interactiveMode(commandsOperator);
        } else {
            batchMode(args, commandsOperator);
        }
    }

}
