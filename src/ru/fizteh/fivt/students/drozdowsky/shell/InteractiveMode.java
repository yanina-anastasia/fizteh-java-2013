package ru.fizteh.fivt.students.drozdowsky.shell;

import java.util.Scanner;
import java.util.Vector;

public class InteractiveMode {
    private PathController workingDirectory;

    public InteractiveMode() {
        workingDirectory = new PathController();
    }

    private String[] scanArgs(Scanner in) {
        Vector<String> args = new Vector<String>();
        Vector<StringBuilder> tempArgs = new Vector<StringBuilder>();
        boolean openQuotes = false;
        boolean lastArgumentEnded = true;
        String temp = "";

        while (true) {
            if (openQuotes) {
                System.out.print("> ");
            }

            if (!in.hasNextLine() || (temp = in.nextLine()).equals("")) {
                if (!openQuotes) {
                    System.exit(0);
                } else {
                    System.err.println("unexpected EOF while looking for matching \'\"\'");
                    System.err.println("syntax error: unexpected end of file");
                    args.clear();
                    return args.toArray(new String[args.size()]);
                }
            }

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
            if (args.length != 0) {
                PacketMode pm = new PacketMode(args, workingDirectory);
                pm.start();
            }
        }
    }
}
