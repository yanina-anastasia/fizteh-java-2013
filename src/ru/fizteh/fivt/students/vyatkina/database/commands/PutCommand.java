package ru.fizteh.fivt.students.vyatkina.database.commands;


import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.vyatkina.shell.Command;


public class PutCommand implements Command {
    Table table;
    public PutCommand (Table table) {
        this.table = table;
    }

    @Override
    public void execute (String[] args) {
        String key = args [0];
        String value = args [1];
        String result = table.put (key, value);
        if (result == null) {
            System.out.println ("new");
        } else {
            System.out.println ("overwrite");
            System.out.println (result);
        }
    }

    @Override
    public String getName () {
        return "put";
    }

    @Override
    public int getArgumentCount () {
        return 2;
    }
}
