package ru.fizteh.fivt.students.asaitgalin.shell;

import ru.fizteh.fivt.students.asaitgalin.shell.commands.*;
import ru.fizteh.fivt.students.asaitgalin.utils.StringUtils;

import java.util.Arrays;
import java.util.Scanner;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        CommandTable table = new CommandTable();
        FilesystemController controller = new FilesystemController();

        table.appendCommand(new CdCommand(controller));
        table.appendCommand(new PwdCommand(controller));
        table.appendCommand(new DirCommand(controller));
        table.appendCommand(new MkdirCommand(controller));
        table.appendCommand(new RmCommand(controller));
        table.appendCommand(new CpCommand(controller));
        table.appendCommand(new MvCommand(controller));
        table.appendCommand(new ExitCommand());

        if (args.length > 0) {
            // Batch mode
            String commandLine = StringUtils.join(Arrays.asList(args), " ");
            try {
                table.executeCommandLine(commandLine);
            } catch (IOException ioe) {
                System.err.println(ioe.getMessage());
                System.exit(1);
            }
        } else {
            // Interactive mode
            Scanner scanner = new Scanner(System.in);
            System.out.print("$ ");
            while (scanner.hasNext()) {
                try {
                    table.executeCommandLine(scanner.nextLine());
                } catch (IOException ioe) {
                    System.err.println(ioe.getMessage());
                }
                System.out.print("$ ");
            }
        }
    }

}
