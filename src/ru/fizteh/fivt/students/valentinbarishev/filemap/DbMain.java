package ru.fizteh.fivt.students.valentinbarishev.filemap;

import java.util.Scanner;
import ru.fizteh.fivt.students.valentinbarishev.shell.Shell;
import ru.fizteh.fivt.students.valentinbarishev.shell.InvalidCommandException;
import ru.fizteh.fivt.students.valentinbarishev.shell.CommandParser;
import ru.fizteh.fivt.students.valentinbarishev.shell.Main;

public class DbMain {
    private static DataBaseTable dbTable;
    private static Shell shell;

    private static void checkDbDir() {
        if (!System.getProperties().containsKey("fizteh.db.dir")) {
            System.err.println("Please set database directory!");
            System.err.println("-Dfizteh.db.dir=<directory name>");
            System.exit(1);
        }
    }

    private static void initShell() {
        shell = new Shell();
        dbTable = new DataBaseTable(System.getProperty("fizteh.db.dir"));

        shell.addCommand(new ShellDbPut(dbTable));
        shell.addCommand(new ShellExit());
        shell.addCommand(new ShellDbGet(dbTable));
        shell.addCommand(new ShellDbRemove(dbTable));
        shell.addCommand(new ShellCreateTable(dbTable));
        shell.addCommand(new ShellDropTable(dbTable));
        shell.addCommand(new ShellUseTable(dbTable));
    }

    private static void packetRun(final String[] args) {
        try {
            CommandParser parser = new CommandParser(args);
            while (!parser.isEmpty()) {
                shell.executeCommand(parser.getCommand());
            }
        } catch (InvalidCommandException|MultiDataBaseException|DataBaseWrongFileFormat e) {
            System.err.println(e.getMessage());
        }
    }

    private static void interactiveRun() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("$ ");
        while (true) {
            try {
                if (!scanner.hasNext()) {
                    throw new ShellExitException("Ctrl + D exit!");
                }

                String command = scanner.nextLine();

                if (Main.checkTerminate(command)) {
                    throw new ShellExitException("Ctrl + D exit or EOF!");
                }

                CommandParser parser = new CommandParser(command);
                if (!parser.isEmpty()) {
                    shell.executeCommand(parser.getCommand());
                }
            } catch (MultiDataBaseException|DataBaseWrongFileFormat|InvalidCommandException e) {
                System.err.println(e.getMessage());
            } finally {
                System.out.print("$ ");
            }
        }
    }

    public static void main(final String[] args) {
        try {
            checkDbDir();
            initShell();

            if (args.length > 0) {
                packetRun(args);
            } else {
                interactiveRun();
            }

        } catch (DataBaseException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (ShellExitException e) {
            if (!dbTable.equals(null)) {
                dbTable.save();
            }
        } finally {
            if (!dbTable.equals(null)) {
                dbTable.save();
            }
        }
    }
}

