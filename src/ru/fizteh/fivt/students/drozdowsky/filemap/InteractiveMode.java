package ru.fizteh.fivt.students.drozdowsky.filemap;

import java.util.Scanner;
import java.util.Vector;
import java.io.File;

public class InteractiveMode {
    private File db;

    public InteractiveMode(File db) {
        this.db = db;
    }

    private String[] scanArgs(Scanner in) {
        Vector<String> args = new Vector<String>();
        Vector<StringBuilder> tempArgs = new Vector<StringBuilder>();
        boolean openQuotes = false;
        boolean lastArgumentEnded = true;
        String temp;

        while (true) {
            if (openQuotes) {
                System.out.print("> ");
            }

            if (!in.hasNextLine()) {
                if (!openQuotes) {
                    System.exit(0);
                } else {
                    System.err.println("unexpected EOF while looking for matching \'\"\'");
                    System.err.println("syntax error: unexpected end of file");
                    args.clear();
                    break;
                }
            }

            temp = in.nextLine();

            for (int i = 0; i < temp.length(); i++) {
                if (temp.charAt(i) == '\"') {
                    openQuotes = !openQuotes;
                } else if (!openQuotes && temp.charAt(i) == ' ') {
                    lastArgumentEnded = true;
                } else {
                    if (lastArgumentEnded) {
                        tempArgs.add(new StringBuilder());
                        lastArgumentEnded = false;
                    }
                    tempArgs.elementAt(tempArgs.size() - 1).append(temp.charAt(i));
                }
            }
            if (!openQuotes) {
                break;
            }
        }
        for (int i = 0; i < tempArgs.size(); i++) {
            args.add(tempArgs.elementAt(i).toString());
        }
        return args.toArray(new String[args.size()]);
    }

    public void start() {
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.print("$ ");
            String[] args = scanArgs(in);
            PacketMode pm = new PacketMode(args, db, false);
            pm.start();
        }
    }
}
