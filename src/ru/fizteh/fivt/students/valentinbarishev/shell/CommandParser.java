package ru.fizteh.fivt.students.valentinbarishev.shell;

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
        if ((!arg.isEmpty()) && (commands.charAt(commands.length() - 1) != ';')) {
            commands.append(';');
        }
    }

    public String[] getCommand() {
        String command = "";
        for (int i = 0; i < commands.length(); ++i) {
            if (commands.charAt(i) == ';') {
                command = commands.substring(0, i);
                commands.replace(0, i + 1, "");
                break;
            }
        }

        StringBuilder builder = new StringBuilder(command.trim());
        for (int i = 0; i < builder.length() - 1;) {
            if ((builder.charAt(i) == ' ') && (builder.charAt(i + 1)) == ' ') {
                builder.deleteCharAt(i);
            }
            ++i;
        }

        command = command.replace("  ", " ");
        return command.split(" ");
    }

    public boolean isEmpty() {
        return (commands.length() == 0);
    }
}
