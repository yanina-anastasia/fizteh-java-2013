package ru.fizteh.fivt.students.anastasyev.shell;

public class DirCommand implements Command {
    private static void dir() {
        String[] fileList = Shell.userDir.list();
        for (String files : fileList) {
            System.out.println(files);
        }
    }

    @Override
    public boolean exec(String[] command) {
        if (command.length != 1) {
            System.err.println("dir: Usage - dir");
            return false;
        }
        try {
            dir();
        } catch (Exception e) {
            System.err.println("dir: wrong path");
            return false;
        }
        return true;
    }

    @Override
    public String commandName() {
        return "dir";
    }
}
