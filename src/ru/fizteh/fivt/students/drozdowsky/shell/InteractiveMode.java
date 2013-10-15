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
        String temp;

        while (true) {
            if (openQuotes) {
                System.out.print("> ");
            }

            if (!in.hasNext()) {
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
            if (args.length == 0) {
                return;
            }

            Utils.executeCommand(args, workingDirectory);
            /*String command = args[0];
            if (command.equals("cd")) {
                ChangeDirectory cd = new ChangeDirectory(workingDirectory, args);
                cd.execute();
            } else if (command.equals("mkdir")) {
                MakeDirectory mkdir = new MakeDirectory(workingDirectory, args);
                mkdir.execute();
            } else if (command.equals("pwd")) {
                PrintWorkingDirectory pwd = new PrintWorkingDirectory(workingDirectory, args);
                pwd.execute();
            } else if (command.equals("rm")) {
                Remove rm = new Remove(workingDirectory, args);
                rm.execute();
            } else if (command.equals("dir")) {
                Directory dir = new Directory(workingDirectory, args);
                dir.execute();
            } else if (command.equals("cp")) {
                Copy cp = new Copy(workingDirectory, args);
                cp.execute();
            } else if (command.equals("mv")) {
                Move mv = new Move(workingDirectory, args);
                mv.execute();
            } else if (command.equals("exit")) {
                return;
            } else {
                    System.err.println(args[0] + " command not found");
            }*/
        }
    }
}
