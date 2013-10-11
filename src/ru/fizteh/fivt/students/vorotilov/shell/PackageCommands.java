package ru.fizteh.fivt.students.vorotilov.shell;

import java.util.LinkedList;
import java.util.Queue;

public class PackageCommands extends ConsoleCommands {

    private Queue<String[]> commandsBuffer;

    public PackageCommands(String[] packageInput) {
        commandsBuffer = new LinkedList<>();
        StringBuilder concatenatedPackageInput = new StringBuilder();
        for (String i : packageInput) {
            concatenatedPackageInput.append(i);
            concatenatedPackageInput.append(" ");
        }
        String[] splittedInput = concatenatedPackageInput.toString().split(";");
        for (String i : splittedInput) {
            commandsBuffer.offer(parseCommand(i));
        }
    }

    @Override
    public String[] getNext() throws NoNextCommand {
        if (commandsBuffer.isEmpty()) {
            throw new NoNextCommand();
        }
        return commandsBuffer.remove();
    }

    @Override
    public boolean hasNext() {
        return !commandsBuffer.isEmpty();
    }

}

