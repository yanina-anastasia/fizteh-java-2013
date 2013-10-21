package ru.fizteh.fivt.students.adanilyak.tools;

import ru.fizteh.fivt.students.adanilyak.commands.Cmd;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

/**
 * User: Alexander
 * Date: 20.10.13
 * Time: 23:15
 */
public class tlShellLogic {
    public static void packageMode(String[] args, Map<String, Cmd> cmdList, PrintStream out, PrintStream err) {
        StringBuilder packOfCommands = new StringBuilder();
        for (String cmdOrArg : args) {
            packOfCommands.append(cmdOrArg).append(" ");
        }
        String inputLine = packOfCommands.toString();
        Vector<String> commandWithArgs = tlCmdParseAndExecute.intoCommandsAndArgs(inputLine, ";");
        try {
            for (String command : commandWithArgs) {
                tlCmdParseAndExecute.execute(command, cmdList);
            }
        } catch (Exception exc) {
            err.println(exc.getMessage());
            System.exit(3);
        }
    }

    public static void interactiveMode(InputStream in, Map<String, Cmd> cmdList, PrintStream out, PrintStream err) {
        Scanner inputStream = new Scanner(in);
        do {
            //Synchronize out and err streams
            // ---
            out.flush();
            err.flush();
            // ---

            out.print("$ ");
            String inputLine = inputStream.nextLine();
            Vector<String> commandWithArgs = tlCmdParseAndExecute.intoCommandsAndArgs(inputLine, ";");
            try {
                for (String command : commandWithArgs) {
                    tlCmdParseAndExecute.execute(command, cmdList);
                }
            } catch (Exception exc) {
                err.println(exc.getMessage());
                System.exit(3);
            }
        } while (!Thread.currentThread().isInterrupted());
    }
}
