package ru.fizteh.fivt.students.dzvonarev.shell;

import java.io.File;
import java.io.IOException;

public class DoCommand {

    public static String getAbsPath(final String Path) throws IOException {
        File newFile = new File(Path);
        if (!newFile.isAbsolute()) {
            newFile = new File(Main.getCurrentDirectory(), Path);
        }
        try {
            return newFile.getCanonicalPath();
        } catch (IOException e) {
            throw new IOException("error: can't get canonical path");
        }
    }

    public static void run(String expression) throws IOException {
        String newExpression = expression.trim();
        int spaceIndex = newExpression.indexOf(' ', 0);
        if (spaceIndex != -1) {
            int index = spaceIndex;
            while (newExpression.indexOf(' ', spaceIndex + 1) == spaceIndex + 1) {
                ++spaceIndex;
            }
            String command = newExpression.substring(0, index);
            if (command.equals("cd")) {
                Cd.changeDir(newExpression, spaceIndex);
            }
            if (command.equals("mkdir")) {
                Mkdir.makeDir(newExpression, spaceIndex);
            }
            if (command.equals("rm")) {
                Remove.deleteObject(newExpression, spaceIndex);
            }
            if (command.equals("mv")) {
                Move.moveObject(newExpression, spaceIndex);
            }
            if (command.equals("cp")) {
                Copy.copyObject(newExpression, spaceIndex);
            }
            if (!command.equals("cd") && !command.equals("mkdir")
                    && !command.equals("rm") && !command.equals("mv")
                    && !command.equals("cp")) {
                throw new IOException("wrong command " + command);
            }
        } else {
            if (newExpression.equals("pwd")) {
                Pwd.printCurrDir();
            }
            if (newExpression.equals("dir")) {
                Dir.listingCurrDir();
            }
            if (newExpression.equals("exit")) {
                Exit.exitShell(0);
            }
            if (!newExpression.equals("pwd") && !newExpression.equals("dir")
                    && !newExpression.equals("exit")) {
                throw new IOException("Wrong command " + newExpression);
            }
        }
    }

}
