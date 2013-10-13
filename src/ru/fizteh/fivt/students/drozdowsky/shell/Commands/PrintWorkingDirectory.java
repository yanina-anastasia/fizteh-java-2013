package ru.fizteh.fivt.students.drozdowsky.shell.Commands;

import ru.fizteh.fivt.students.drozdowsky.shell.PathController;

public class PrintWorkingDirectory {
    private PathController path;
    private String[] args;

    public PrintWorkingDirectory(PathController path, String[] args) {
        this.path = path;
        this.args = args;
    }

    public void execute() {
        System.out.println(path.toString());
    }
}
