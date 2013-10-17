package ru.fizteh.fivt.students.vishnevskiy.filemap.commands;


import ru.fizteh.fivt.students.vishnevskiy.filemap.Command;
import ru.fizteh.fivt.students.vishnevskiy.filemap.FileMapException;
import ru.fizteh.fivt.students.vishnevskiy.filemap.SingleFileMap;

public class Exit implements Command {
    private static final String NAME = "exit";
    public Exit() {}
    public String getName() {
        return NAME;
    }
    public void execute(SingleFileMap singleFileMap, String[] args) throws FileMapException {
        if (args.length > 0) {
            throw new FileMapException("exit: no arguments needed");
        }
        singleFileMap.write();
        System.out.println("exit");
        System.exit(0);
    }
}
