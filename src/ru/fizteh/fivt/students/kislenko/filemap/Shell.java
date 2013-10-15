package ru.fizteh.fivt.students.kislenko.filemap;

import java.util.Scanner;

public class Shell {
    private State state;

    public Shell(State startingState) {
        state = startingState;
    }

    public void interactiveMode() {
        Scanner scan = new Scanner(System.in);
        CmdLauncher launcher = new CmdLauncher();
        boolean exitFlag = false;
        while (!exitFlag) {
            System.out.print("$ ");
            String input = scan.nextLine().trim();
            String[] commands = input.split("\\s*;\\s*");
            try {
                for (String command : commands) {
                    if (command.equals("exit")) {
                        exitFlag = true;
                        break;
                    }
                    launcher.launch(state, command);
                }
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
                launcher.launch(state, command);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
    }
}