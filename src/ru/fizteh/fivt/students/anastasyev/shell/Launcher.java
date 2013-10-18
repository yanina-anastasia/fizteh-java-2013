package ru.fizteh.fivt.students.anastasyev.shell;

import ru.fizteh.fivt.students.anastasyev.filemap.*;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Vector;

public class Launcher {
    private Vector<Command> allCommands;
    private State state;

    private void trySaveState() throws IOException {
        if (state.getClass().equals(FileMap.class)) {
            ((FileMap) state).saveFileMap();
        }
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
                result = allCommands.elementAt(i).exec(state, commands);
                break;
            }
        }
        if (i >= allCommands.size()) {
            System.err.println("Wrong command " + arg);
            return false;
        }
        return result;
    }

    public void interactiveMode() {
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
                    trySaveState();
                } catch (IOException e1) {
                    System.err.println(e1.getMessage());
                }
                System.exit(1);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void packageMode(final String[] args) {
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
                        trySaveState();
                    } catch (IOException e1) {
                        System.err.println(e1.getMessage());
                    }
                    System.exit(1);
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            try {
                trySaveState();
            } catch (IOException e1) {
                System.err.println(e1.getMessage());
            }
            System.exit(1);
        }
    }

    public Launcher(State newState) {
        state = newState;
        allCommands = state.getCommands();
    }
}

