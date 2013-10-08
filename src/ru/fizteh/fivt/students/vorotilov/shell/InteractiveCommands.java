package ru.fizteh.fivt.students.vorotilov.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class InteractiveCommands extends ConsoleCommands {

    protected BufferedReader inputStream;

    InteractiveCommands() {
        inputStream = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public String[] getNextCommand() throws IOException {
        System.out.print("$ ");
        return parseCommand(inputStream.readLine());
    }
}
