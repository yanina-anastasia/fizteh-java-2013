package ru.fizteh.fivt.students.kislenko.shell;

import java.util.Scanner;

public class Shell {
    public static Location loc = new Location();

    public void interactiveMode() {
        Scanner scan = new Scanner(System.in);
        while (true) {
            System.out.print(loc.getPath().toString() + "$ ");
            try {
                String command = scan.nextLine().trim();
                if (command.equals("exit")) {
                    break;
                }
                CmdLauncher.launch(command);
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
        String[] commands = input.split(";");
        for (String command : commands) {
            try {
                command = command.trim();
                if (command.equals("exit")) {
                    break;
                }
                CmdLauncher.launch(command);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
    }
}