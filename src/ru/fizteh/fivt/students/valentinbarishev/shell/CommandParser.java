package ru.fizteh.fivt.students.valentinbarishev.shell;

/**
 * Created with IntelliJ IDEA.
 * User: Valik
 * Date: 02.10.13
 * Time: 23:42
 * To change this template use File | Settings | File Templates.
 */

public class CommandParser {
    private StringBuilder commands;

    public CommandParser(String[] args) {
        commands = new StringBuilder();
        for (int i = 0; i < args.length; ++i) {
            commands.append(" ").append(args[i]);
        }
        if (commands.charAt(commands.length() - 1) != ';') {
            commands.append(';');
        }
    }

    public CommandParser(String arg) {
        commands = new StringBuilder(arg);
        if (commands.charAt(commands.length() - 1) != ';') {
            commands.append(';');
        }
    }

    public String[] getCommand() {
        String command = "";
        for (int i = 0; i < commands.length(); ++i) {
            if (commands.charAt(i) == ';') {
                command = commands.substring(0, i);
                commands.replace(0, i + 1,"");
                break;
            }
        }
        command = command.trim();
        command = command.replace("  "," ");
        return command.split(" ");
    }

    public boolean isEmpty() {
        return (commands.length() == 0);
    }
}
