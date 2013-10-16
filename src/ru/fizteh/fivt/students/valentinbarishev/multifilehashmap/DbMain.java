package ru.fizteh.fivt.students.valentinbarishev.multifilehashmap;

import java.util.Scanner;
import ru.fizteh.fivt.students.valentinbarishev.filemap.DataBaseException;
import ru.fizteh.fivt.students.valentinbarishev.filemap.DataBaseWrongFileFormat;
import ru.fizteh.fivt.students.valentinbarishev.shell.Shell;
import ru.fizteh.fivt.students.valentinbarishev.shell.InvalidCommandException;
import ru.fizteh.fivt.students.valentinbarishev.shell.ShellExit;
import ru.fizteh.fivt.students.valentinbarishev.shell.CommandParser;
import ru.fizteh.fivt.students.valentinbarishev.shell.Main;

public class DbMain {
    public static void main(final String[] args) {
        try {
            Shell shell = new Shell();
            if (!System.getProperties().containsKey("fizteh.db.dir")) {
                System.err.println("Please set database directory!");
                System.err.println("-Dfizteh.db.dir=<directory name>");
                System.exit(1);
            }

            DataBaseTable dbTable = new DataBaseTable(System.getProperty("fizteh.db.dir"));

            shell.addCommand(new ShellDbPut(dbTable));
            shell.addCommand(new ShellExit());
            shell.addCommand(new ShellDbGet(dbTable));
            shell.addCommand(new ShellDbRemove(dbTable));
            shell.addCommand(new ShellCreateTable(dbTable));
            shell.addCommand(new ShellDropTable(dbTable));
            shell.addCommand(new ShellUseTable(dbTable));


            if (args.length > 0) {
                CommandParser parser = new CommandParser(args);
                while (!parser.isEmpty()) {
                    shell.executeCommand(parser.getCommand());
                }
            } else {
                Scanner scanner = new Scanner(System.in);
                System.out.print("$ ");
                while (true) {
                    try {
                        String command = scanner.nextLine();

                        if (Main.checkTerminate(command)) {
                            System.exit(0);
                        }

                        CommandParser parser = new CommandParser(command);
                        if (!parser.isEmpty()) {
                            shell.executeCommand(parser.getCommand());
                        }
                    } catch (InvalidCommandException e) {
                        System.err.println(e.getMessage());
                    } catch (MultiDataBaseException e) {
                        System.err.println(e.getMessage());
                    } catch (DataBaseWrongFileFormat e) {
                        System.err.println(e.getMessage());
                    } finally {
                        System.out.print("$ ");
                    }
                }
            }

        } catch (InvalidCommandException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (DataBaseException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
