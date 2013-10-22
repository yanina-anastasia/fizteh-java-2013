package ru.fizteh.fivt.students.drozdowsky.filemap;

import java.util.Scanner;
import java.util.Vector;

public class InteractiveMode {
    private Database db;

    public InteractiveMode(Database db) {
        this.db = db;
    }

    private String[] scanArgs(Scanner in) {
        Vector<String> args = new Vector<String>();
        Vector<StringBuilder> tempArgs = new Vector<StringBuilder>();
        boolean lastArgumentEnded = true;
        String[] temp = new String[1];

        if (!in.hasNextLine()) {
            System.exit(0);
        }
        temp[0] = in.nextLine();

        return temp;
    }


    public void start() {
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.print("$ ");
            String[] args = scanArgs(in);
            if (args.length != 0) {
                PacketMode pm = new PacketMode(db, args, false);
                pm.start();
            }
        }
    }
}
