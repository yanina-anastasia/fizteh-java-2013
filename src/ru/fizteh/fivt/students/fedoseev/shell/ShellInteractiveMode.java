package ru.fizteh.fivt.students.fedoseev.shell;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ShellInteractiveMode extends Shell {
    public ShellInteractiveMode(File dir) {
        super(dir);
    }

    public void run() throws IOException {
        Scanner scanner = new Scanner(System.in);

        while (!Thread.currentThread().isInterrupted()) {
            System.out.print(state.getCurState().toString() + "$ ");

            String inputString = scanner.nextLine().trim();
            String[] input = inputString.split("\\s*;\\s*");

            for (String cmd : input) {
                cmd = cmd.trim();

                int end;
                if ((end = cmd.indexOf(" ")) == -1) {
                    end = cmd.length();
                }

                try {
                    COMMANDS.get(cmd.substring(0, end)).execute(getCommandArguments(cmd), state);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }
}
