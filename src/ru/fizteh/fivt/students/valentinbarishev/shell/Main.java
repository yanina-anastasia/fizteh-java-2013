package ru.fizteh.fivt.students.valentinbarishev.shell;

import java.io.IOException;
import java.io.InputStream;


public class Main {
    static final int END_OF_INPUT = -1;
    static final int END_OF_TRANSMISSION = 4;

    private static boolean isTerminativeSymbol(int character) {
        return ((character == END_OF_INPUT) || (character == END_OF_TRANSMISSION));
    }

    public static void main(String[] args) {
        try {
            Shell shell = new Shell();
            Context context = new Context();

            shell.addCommand(new ShellPwd(context));
            shell.addCommand(new ShellCd(context));
            shell.addCommand(new ShellMkdir(context));
            shell.addCommand(new ShellDir(context));
            shell.addCommand(new ShellRm(context));
            shell.addCommand(new ShellCp(context));
            shell.addCommand(new ShellMv(context));
            shell.addCommand(new ShellExit());

            if (args.length > 0) {
                CommandParser parser = new CommandParser(args);
                while (!parser.isEmpty()) {
                    shell.executeCommand(parser.getCommand());
                }
            } else {
                InputStream input = System.in;
                System.out.print("$ ");
                while (true) {
                    try {
                        int character;
                        StringBuilder commands = new StringBuilder();

                        while ((!isTerminativeSymbol(character = input.read())) && (character != System.lineSeparator().charAt(1))) {
                            commands.append((char) character);
                        }

                        if (isTerminativeSymbol(character)) {
                            System.exit(0);
                        }

                        CommandParser parser = new CommandParser(commands.toString());
                        if (!parser.isEmpty()) {
                            shell.executeCommand(parser.getCommand());
                        }
                    } catch (InvalidCommandException e) {
                        System.err.println(e.getMessage());
                    } finally {
                        System.out.print("$ ");
                    }
                }

            }

        } catch (InvalidCommandException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't read current directory");
            System.exit(1);
        }
    }
}
