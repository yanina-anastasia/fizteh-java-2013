package ru.fizteh.fivt.students.drozdowsky.shell;

import java.util.Scanner;

public class InteractiveMode {
    private PathController workingDirectory;

    public InteractiveMode() {
        workingDirectory = new PathController();
    }

    public void start() {
        Scanner in = new Scanner(System.in);
        while (true) {
            String[] args = ShellUtils.scanArgs(in);
            if (args.length != 0) {
                PacketMode pm = new PacketMode(args, workingDirectory);
                pm.start();
            }
        }
    }
}
