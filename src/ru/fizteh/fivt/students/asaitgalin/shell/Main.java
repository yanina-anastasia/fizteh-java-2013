package ru.fizteh.fivt.students.asaitgalin.shell;

import ru.fizteh.fivt.students.asaitgalin.shell.commands.*;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        CommandTable table = new CommandTable();
        FilesystemController controller = null;
        try {
            controller = new FilesystemController();
        } catch (IOException ioe) {
            System.err.println("Failed to get current directory. Exiting...");
            System.exit(-1);
        }
        table.appendCommand(new CdCommand(controller));
        table.appendCommand(new PwdCommand(controller));
        table.appendCommand(new DirCommand(controller));
        table.appendCommand(new MkdirCommand(controller));
        table.appendCommand(new RmCommand(controller));
        table.appendCommand(new CpCommand(controller));
        table.appendCommand(new MvCommand(controller));
        table.appendCommand(new ExitCommand());

        if (args.length > 0) {
            for (String s: args) {
                try {
                    table.executeCommands(s);
                } catch (UnknownCommandException uce) {
                    System.err.println(uce.getMessage());
                }
            }
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.print("$ ");
            while (scanner.hasNext()) {
                try {
                    table.executeCommands(scanner.nextLine());
                } catch (UnknownCommandException uce) {
                    System.err.println(uce.getMessage());
                } finally {
                    System.out.print("$ ");
                }
            }
        }
    }

}
