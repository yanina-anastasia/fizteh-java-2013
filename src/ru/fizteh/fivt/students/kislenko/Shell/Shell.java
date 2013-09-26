package ru.fizteh.fivt.students.kislenko.Shell;
/**
 * Created with IntelliJ IDEA.
 * User: Александр
 * Date: 25.09.13
 * Time: 0:50
 * To change this template use File | Settings | File Templates.
 */

import java.io.IOException;
import java.lang.*;
import java.io.PrintStream;
import java.util.*;

public class Shell {
    private static void main(String[] Args) throws IOException{
        int code;
        if(Args.length==0) {
            code = interactiveMode();
        } else {
            code = packageMode();
        }
        if (code == -1) {
            throw new IOException("Wrong command.");
        }
        System.exit(0);
    }

    private static int cd(String arg) {
        return 0;
    }

    private static int mkdir(String arg) {
        return 0;
    }

    private static int pwd() {
        return 0;
    }

    private static int rm(String arg) {
        return 0;
    }

    private static int cp(String arg1, String arg2) {
        return 0;
    }

    private static int mv(String arg1, String arg2) {
        return 0;
    }

    private static int dir() {
        return 0;
    }

    private static int exit() {
        return 0;
    }

    private static int interactiveMode() {
        PrintStream ps = new PrintStream(System.out);
        Scanner scan = new Scanner(System.in);
        ps.print("$ ");
        do {
            String command = scan.next();
            if(command.matches("cd \\S*")) {
                String arg = command.substring(4);
                cd(arg);
            } else if(command.matches("mkdir \\S*")) {
                String arg = command.substring(7);
                mkdir(arg);
            } else if(command.matches("pwd")) {
                pwd();
            } else if(command.matches("rm \\S*")) {
                String arg = command.substring(4);
                rm(arg);
            } else if(command.matches("cp \\S* \\S*")) {
                int endOfFirstArg = command.indexOf(' ', 4);
                String arg1 = command.substring(4, endOfFirstArg - 1);
                String arg2 = command.substring(endOfFirstArg+1);
                cp(arg1, arg2);
            } else if(command.matches("mv \\S* \\S*")) {
                int endOfFirstArg = command.indexOf(' ', 4);
                String arg1 = command.substring(4, endOfFirstArg - 1);
                String arg2 = command.substring(endOfFirstArg+1);
                mv(arg1, arg2);
            } else if(command.matches("dir")) {
                dir();
            } else if(command.matches("exit")) {
                return 0;
            } else {
                return -1;
            }
        } while (true);
    }

    private static int packageMode() {
        PrintStream ps = new PrintStream(System.out);
        Scanner scan = new Scanner(System.in);
        return 0;
    }
}