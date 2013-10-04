package ru.fizteh.fivt.students.valentinbarishev.shell;

import java.io.IOException;
import java.util.Scanner;

public class Main {

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

            if (args.length > 0){
                CommandParser parser = new CommandParser(args);
                while (!parser.isEmpty()) {
                    shell.executeCommand(parser.getCommand());
                }
            } else {
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    System.out.print("$ ");
                    try {
                        CommandParser parser = new CommandParser(scanner.nextLine());
                        shell.executeCommand(parser.getCommand());
                    } catch (InvalidCommandException e) {
                        System.err.println(e.getMessage());
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
