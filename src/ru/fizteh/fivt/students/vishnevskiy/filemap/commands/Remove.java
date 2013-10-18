package ru.fizteh.fivt.students.vishnevskiy.filemap.commands;


import ru.fizteh.fivt.students.vishnevskiy.filemap.Command;
import ru.fizteh.fivt.students.vishnevskiy.filemap.FileMapException;
import ru.fizteh.fivt.students.vishnevskiy.filemap.SingleFileMap;


public class Remove implements Command {
    private static final String NAME = "remove";
    public Remove() {}
    public String getName() {
        return NAME;
    }
    public void execute(SingleFileMap singleFileMap, String[] args) throws FileMapException {
        if (args.length == 0) {
            throw new FileMapException("remove: arguments expected");
        }
        if (args.length > 1) {
            throw new FileMapException("remove: wrong number of arguments");
        }
        int status = singleFileMap.remove(args[0]);
        if (status == 0) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }
}
