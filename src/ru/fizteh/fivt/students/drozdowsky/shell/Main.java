package ru.fizteh.fivt.students.drozdowsky.shell;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            InteractiveMode im = new InteractiveMode();
            im.start();
        } else {
            PacketMode pm = new PacketMode(args);
            pm.start();
        }
    }
}
