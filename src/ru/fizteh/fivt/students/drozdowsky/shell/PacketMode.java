package ru.fizteh.fivt.students.drozdowsky.shell;

import java.util.ArrayList;

public class PacketMode {
    private PathController workingDirectory;
    String[] args;

    public PacketMode(String[] args) {
        workingDirectory = new PathController();
        this.args = args;
    }

    public void start() {
        ArrayList<String> tempArgs = new ArrayList<String>();
        for (int i = 0; i < args.length; i++) {
            tempArgs.add(args[i]);
            if (args[i].charAt(args[i].length() - 1) == ';') {
                String temp = tempArgs.get(tempArgs.size() - 1);
                tempArgs.remove(tempArgs.size() - 1);
                tempArgs.add(temp.substring(0, temp.length() - 1));
                Utils.executeCommand(tempArgs.toArray(new String[tempArgs.size()]), workingDirectory);
                tempArgs.clear();
            }
        }
        if (tempArgs.size() != 0) {
            Utils.executeCommand(tempArgs.toArray(new String[tempArgs.size()]), workingDirectory);
        }
    }
}
