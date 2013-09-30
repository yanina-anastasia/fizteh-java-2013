package ru.fizteh.fivt.students.belousova.shell;

import java.io.IOException;

public class StringHandler {
    public static void handle(String s) {
        String[] commands = s.split(";");

        for (String command : commands) {
            String[] components = command.split(" ");
            try {
                for (String component : components) {
                    if (component.isEmpty()) {
                        continue;
                    }
                    if (!MainShell.commandList.containsKey(component)) {
                        throw new IOException("Invalid command");
                    }
                    MainShell.commandList.get(component).execute(command);
                    break;
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
