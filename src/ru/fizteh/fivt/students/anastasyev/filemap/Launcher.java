package ru.fizteh.fivt.students.anastasyev.filemap;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Vector;
import ru.fizteh.fivt.students.anastasyev.shell.Command;

public class Launcher {
    private Vector<Command> allCommands;
    private FileMap fileMap;

    public FileMap getFileMap() {
        return fileMap;
    }

    private boolean launch(final String arg) throws IOException {
        if (arg.equals("")) {
            return true;
        }
        String[] commands = arg.split("\\s+", 3);
        boolean result = false;
        int i = 0;
        for (; i < allCommands.size(); ++i) {
            if (allCommands.elementAt(i).commandName().equals(commands[0])) {
                result = allCommands.elementAt(i).exec(commands);
                break;
            }
        }
        if (i >= allCommands.size()) {
            System.err.println("Wrong command " + arg);
            return false;
        }
        return result;
    }

    private void interactiveMode() {
        Scanner scan = new Scanner(System.in);
        while (true) {
            System.err.flush();
            System.out.print("$ ");
            try {
                String commands = scan.nextLine().trim();
                String[] allArgs = commands.split(";");
                for (String arg : allArgs) {
                    if (!arg.equals("")) {
                        if (!launch(arg.trim())) {
                            break;
                        }
                    }
                }
            } catch (NoSuchElementException e) {
                try {
                    fileMap.saveFileMap();
                } catch (IOException e1) {
                    System.err.println(e1.getMessage());
                }
                System.exit(1);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private void packageMode(final String[] args) {
        StringBuilder packageCommandsNames = new StringBuilder();
        for (String arg : args) {
            packageCommandsNames.append(arg).append(" ");
        }
        String commands = packageCommandsNames.toString();
        String[] allArgs = commands.split(";");
        try {
            for (String arg : allArgs) {
                if (!launch(arg.trim())) {
                    try {
                        fileMap.saveFileMap();
                    } catch (IOException e1) {
                        System.err.println(e1.getMessage());
                    }
                    System.exit(1);
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            try {
                fileMap.saveFileMap();
            } catch (IOException e1) {
                System.err.println(e1.getMessage());
            }
            System.exit(1);
        }
    }

    public void fileMapLauncher(String[] args) {
        if (System.getProperty("fizteh.db.dir") == null) {
            System.err.println("Set home data base's directory");
            System.err.println("Use: -Dfizteh.db.dir=<directory>");
            System.exit(1);
        }
        fileMap = new FileMap(System.getProperty("fizteh.db.dir") + File.separator + "db.dat");
        allCommands = fileMap.getCommands();
        fileMap.addCommand(new PutCommand(this));
        fileMap.addCommand(new GetCommand(this));
        fileMap.addCommand(new RemoveCommand(this));
        fileMap.addCommand(new FileMapExitCommand(this));
        if (args.length == 0) {
            interactiveMode();
        } else {
            packageMode(args);
        }
        try {
            fileMap.saveFileMap();
        } catch (IOException e1) {
            System.err.println(e1.getMessage());
        }
    }
}

