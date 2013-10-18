package ru.fizteh.fivt.students.dmitryKonturov.shell;

public class Shell {

    public static void main(String[] mainArgs) {

        ShellWorkingWithFileSystem shell = new ShellWorkingWithFileSystem(System.getProperty("user.dir"));

        if (mainArgs.length > 0) {
            StringBuilder builder = new StringBuilder();
            for (String arg : mainArgs) {
                builder.append(arg);
                builder.append(" ");
            }
            try {
                shell.executeQuery(builder.toString());
            } catch (ShellEmulator.ShellException se) {
                System.err.println(se);
                System.exit(1);
            }
        } else {
            shell.interactiveMode();
        }

    }
}
