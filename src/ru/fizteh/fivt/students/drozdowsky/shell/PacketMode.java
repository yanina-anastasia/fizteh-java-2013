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
        StringBuilder concatenateArgs = new StringBuilder();

        for (String arg : args) {
            concatenateArgs.append(arg);
            concatenateArgs.append(" ");
        }
        concatenateArgs.append(';');

        ArrayList<String> tempArgs = new ArrayList<String>();
        int last = -1;

        for (int j = 0; j < concatenateArgs.length(); j++) {
            if (concatenateArgs.charAt(j) == ';' || concatenateArgs.charAt(j) == ' ' || concatenateArgs.charAt(j) == '\t') {
                if (last + 1 != j) {
                    tempArgs.add(concatenateArgs.substring(last + 1, j));
                }
                last = j;

                if (concatenateArgs.charAt(j) == ';' && tempArgs.size() != 0) {
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
