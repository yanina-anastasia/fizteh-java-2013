package ru.fizteh.fivt.students.adanilyak.filemap;

/**
 * User: Alexander
 * Date: 15.10.13
 * Time: 19:32
 */

import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

public class Main {
    public static Vector<String> intoCommandsAndArgs(String cmd, String delimetr) {
        String[] tokens = cmd.split(delimetr);
        Vector<String> result = new Vector<String>();
        for (int i = 0; i < tokens.length; i++) {
            if (!tokens[i].equals("") && !tokens[i].matches("\\s+")) {
                result.add(tokens[i]);
            }
        }
        return result;
    }

    private static boolean execute(String cmd, Shell shell) throws IOException {
        Vector<String> cmdAndArgs = intoCommandsAndArgs(cmd, " ");
        try {
            RequestCommandTypeFileMap cmdType = RequestCommandTypeFileMap.getType(cmdAndArgs.get(0));
            switch (cmdType) {
                case put:
                    CmdPut curPut = new CmdPut();
                    if (cmdAndArgs.size() != curPut.getAmArgs() + 1) {
                        throw new IOException("Wrong amount of arguments");
                    } else {
                        curPut.work(cmdAndArgs.get(1), cmdAndArgs.get(2), shell);
                    }
                    break;
                case get:
                    CmdGet curGet = new CmdGet();
                    if (cmdAndArgs.size() != curGet.getAmArgs() + 1) {
                        throw new IOException("Wrong amount of arguments");
                    } else {
                        curGet.work(cmdAndArgs.get(1), shell);
                    }
                    break;
                case remove:
                    CmdRemove curRemove = new CmdRemove();
                    if (cmdAndArgs.size() != curRemove.getAmArgs() + 1) {
                        throw new IOException("Wrong amount of arguments");
                    } else {
                        curRemove.work(cmdAndArgs.get(1), shell);
                    }
                    break;
                case exit:
                    shell.exit();
                    return false;
            }
        } catch (Exception exc) {
            System.err.println(cmdAndArgs + ": " + exc.getMessage());
            if (shell.getTypeOfRunning()) {
                System.exit(2);
            }
        }
        return true;
    }

    public static void main(String[] args) {
        Shell shell = new Shell("db.dat");

        if (args.length == 0) {
            shell.setInteractiveType();
            Scanner inputStream = new Scanner(System.in);
            boolean continueCycle = true;
            do {
                //Synchronize out and err streams
                // ---
                System.out.flush();
                System.err.flush();
                // ---

                System.out.print("$ ");
                String inputLine = inputStream.nextLine();
                inputLine.trim();
                Vector<String> commands = intoCommandsAndArgs(inputLine, ";");
                try {
                    for (String cmd : commands) {
                        if (!execute(cmd, shell)) {
                            continueCycle = false;
                        }
                    }
                    if (commands.isEmpty()) {
                        System.err.println("There are no suitable commands in line");
                    }
                } catch (Exception exc) {
                    System.err.println(exc.getMessage());
                    System.exit(1);
                }
            } while (continueCycle);
        } else {
            shell.setPackageType();
            StringBuilder packOfCommands = new StringBuilder();
            for (String cmdOrArg : args) {
                packOfCommands.append(cmdOrArg).append(" ");
            }
            String inputLine = packOfCommands.toString();
            Vector<String> commands = intoCommandsAndArgs(inputLine, ";");
            try {
                for (String cmd : commands) {
                    if (!execute(cmd, shell)) {
                        break;
                    }
                }
            } catch (Exception exc) {
                System.err.println(exc.getMessage());
                System.exit(1);
            }
        }

        shell.testPrint();
    }
}