package ru.fizteh.fivt.students.drozdowsky.shell;

import java.util.ArrayList;

public class PacketMode {
    private PathController workingDirectory;
    private String[] args;
    private boolean exitOnFailure;

    public PacketMode(String[] args) {
        workingDirectory = new PathController();
        this.args = args;
        exitOnFailure = true;
    }

    public PacketMode(String[] args, PathController path) {
        workingDirectory = path;
        this.args = args;
        exitOnFailure = false;
    }

    public void start() {
        ArrayList<String[]> commands = Parser.parse(args, new String[0]);

        for (String[] command : commands) {
            if (!Utils.executeCommand(command, workingDirectory)) {
                if (exitOnFailure) {
                    System.exit(1);
                } else {
                    return;
                }
            }
        }
    }
}
