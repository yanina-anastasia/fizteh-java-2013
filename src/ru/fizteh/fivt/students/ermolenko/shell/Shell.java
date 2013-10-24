package ru.fizteh.fivt.students.ermolenko.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

public class Shell {

    private ShellState state;

    public Shell(File currentDirectory) {
        state = new ShellState();
        currentDirectory = currentDirectory.getAbsoluteFile();
        state.setPath((currentDirectory.toPath()));
    }

    public Shell() {
    }

    public ShellState getState() {
        return state;
    }

    public void setState(Path inState) {

        state.setPath(inState);
    }

    public void batchState(String[] args, Executor exec) throws IOException {

        StringBuilder tmp = new StringBuilder();
        for (String arg : args) {
            tmp.append(arg).append(" ");
        }

        String[] command = tmp.toString().split("\\;");

        String cmd = "";

        for (int i = 0; i < command.length - 1; ++i) {

            cmd = command[i].trim();
            if (cmd.equals("exit")) {
                break;
            }
            try {
                exec.execute(this, cmd);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
    }

    public void interactiveState(Executor exec) throws IOException {

        Scanner scanner = new Scanner(System.in);

        String[] cmd;
        while (true) {
            System.out.println("4");
            System.out.print("$ ");
            cmd = scanner.nextLine().trim().split("\\s*;\\s*");

            try {
                for (String aCmd : cmd) {
                    exec.execute(this, aCmd);
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }
}