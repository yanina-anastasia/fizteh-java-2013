package ru.fizteh.fivt.students.kislenko.shell;

import java.nio.file.Path;
import java.util.Scanner;

public class Shell {
    private State state = new State();

    public Shell(State startingDir) {
        state.changePath(startingDir.getPath());
    }

    public void setState(Path p) {
        state.changePath(p);
    }

    public Path getState() {
        return state.getPath();
    }

    public void interactiveMode() {
        Scanner scan = new Scanner(System.in);
        CmdLauncher launcher = new CmdLauncher();
        while (true) {
            System.out.print(state.getPath().toString() + "$ ");
            String command = scan.nextLine().trim();
            if (command.equals("exit")) {
                break;
            }
            try {
                launcher.launch(this, command);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void batchMode(String[] args) {
        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            sb.append(arg).append(" ");
        }
        String input = sb.toString();
        String[] commands = input.split("\\s*;\\s*");
        CmdLauncher launcher = new CmdLauncher();
        for (String command : commands) {
            command = command.trim();
            if (command.equals("exit")) {
                break;
            }
            try {
                launcher.launch(this, command);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
    }
}