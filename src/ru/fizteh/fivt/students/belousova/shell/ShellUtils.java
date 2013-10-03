package ru.fizteh.fivt.students.belousova.shell;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class ShellUtils {
    public static void batchMode(String[] args, ShellState state) {
        StringBuilder sb = new StringBuilder();
        for (String si : args) {
            sb.append(si);
            sb.append(" ");
        }
        String s = sb.toString();
        stringHandle(s, state);
    }

    public static void interactiveMode(InputStream inputStream, ShellState state) {

        while (true) {

            System.out.print("$ ");
            Scanner scanner = new Scanner(inputStream);
            String s = scanner.nextLine();
            stringHandle(s, state);
        }
    }

    public static void stringHandle(String s, ShellState state) {
        String[] commands = s.split("\\s*;\\s*");

        for (String command : commands) {
            String[] tokens = command.split("\\s+");
            try {
                String commandName = tokens[0];
                if (!state.commandList.containsKey(commandName)) {
                    throw new IOException("Invalid command");
                }
                state.commandList.get(commandName).execute(command);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
