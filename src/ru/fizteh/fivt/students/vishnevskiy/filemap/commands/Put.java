package ru.fizteh.fivt.students.vishnevskiy.filemap.commands;


import ru.fizteh.fivt.students.vishnevskiy.filemap.Command;
import ru.fizteh.fivt.students.vishnevskiy.filemap.FileMapException;
import ru.fizteh.fivt.students.vishnevskiy.filemap.SingleFileMap;

public class Put implements Command {
    private static final String NAME = "put";

    public Put() {}

    public String getName() {
        return NAME;
    }

    public int getArgsNum() {
        return 2;
    }

    public void execute(SingleFileMap singleFileMap, String[] args) throws FileMapException {
        if (args.length < 2) {
            throw new FileMapException("put: arguments expected");
        }
        if (args.length > 2) {
            throw new FileMapException("put: wrong number of arguments");
        }
        String status = singleFileMap.put(args[0], args[1]);
        if (status == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(status);
        }
    }
}
