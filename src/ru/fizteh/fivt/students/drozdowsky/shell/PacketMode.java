package ru.fizteh.fivt.students.drozdowsky.shell;

import java.util.ArrayList;

public class PacketMode {
    private PathController workingDirectory;
    String[] args;
    boolean exitOnFailure;

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
        StringBuilder totalArgs = new StringBuilder();

        for (String arg : args) {
            totalArgs.append(arg);
            totalArgs.append(" ");
        }
        totalArgs.append(';');

        ArrayList<String> tempArgs = new ArrayList<String>();
        int last = -1;

        for (int j = 0; j < totalArgs.length(); j++) {
            if (totalArgs.charAt(j) == ';' || totalArgs.charAt(j) == ' ' || totalArgs.charAt(j) == '\t') {
                if (last + 1 != j) {
                    tempArgs.add(totalArgs.substring(last + 1, j));
                }
                last = j;

                if (totalArgs.charAt(j) == ';' && tempArgs.size() != 0) {
                    if (!Utils.executeCommand(tempArgs.toArray(new String[tempArgs.size()]), workingDirectory)) {
                        if (exitOnFailure) {
                            System.exit(1);
                        }
                    }
                    tempArgs.clear();
                }
            }
        }
    }
}
