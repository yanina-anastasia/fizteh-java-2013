package ru.fizteh.fivt.students.piakovenko.filemap;

import ru.fizteh.fivt.students.piakovenko.shell.Commands;
import ru.fizteh.fivt.students.piakovenko.shell.MyException;


public class Put implements Commands {
    private final String name = "put";
    public String getName() {
        return name;
    }
    public void perform(String[] args) throws MyException {
        if (args.length != 3) {
            throw new MyException(new Exception("Wrong number of arguments! Usage: get <keyValue>"));
        }
        DataBase.put(args[1], args[2]);
    }
}
