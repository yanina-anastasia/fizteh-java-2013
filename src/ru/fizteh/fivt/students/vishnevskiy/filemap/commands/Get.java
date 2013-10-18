package ru.fizteh.fivt.students.vishnevskiy.filemap.commands;


import ru.fizteh.fivt.students.vishnevskiy.filemap.Command;
import ru.fizteh.fivt.students.vishnevskiy.filemap.FileMapException;
import ru.fizteh.fivt.students.vishnevskiy.filemap.SingleFileMap;

public class Get implements Command {
    private static final String NAME = "get";
    public Get() {}
    public String getName() {
        return NAME;
    }
    public void execute(SingleFileMap singleFileMap, String[] args) throws FileMapException {
        if (args.length == 0) {
            throw new FileMapException("get: arguments expected");
        }
        if (args.length > 1) {
            throw new FileMapException("get: wrong number of arguments");
        }
        String value = singleFileMap.get(args[0]);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(value);
        }
    }
}
