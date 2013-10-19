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
        args[args.length - 1] = args[args.length - 1] + ";";

        ArrayList<String> tempArgs = new ArrayList<String>();
        for (String arg : args) {
            int last = -1;
            for (int i = 0; i < arg.length(); i++) {
                if (arg.charAt(i) == ';') {
                    if (last + 1 != i) {
                        tempArgs.add(arg.substring(last + 1, i));
                        last = i;
                        if (!Utils.executeCommand(tempArgs.toArray(new String[tempArgs.size()]), workingDirectory)) {
                            if (exitOnFailure) {
                                System.exit(1);
                            }
                        }
                    }
                    tempArgs.clear();
                }
            }
            if (last + 1 != arg.length()) {
                tempArgs.add(arg.substring(last + 1, arg.length()));
            }
        }
    }
}
