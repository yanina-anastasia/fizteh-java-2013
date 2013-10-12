package ru.fizteh.fivt.students.vorotilov.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class InteractiveCommands extends ConsoleCommands {

    protected BufferedReader inputStream;

    public InteractiveCommands() {
        inputStream = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public String[] getNext() throws IOException, ExitCommand {
        System.out.print("$ ");
        String newLine = inputStream.readLine();
        if (newLine == null) {
            throw new ExitCommand();
        }
        return parseCommand(newLine);
    }

    @Override
    public boolean hasNext() throws IOException {
        return true;
    }

}
