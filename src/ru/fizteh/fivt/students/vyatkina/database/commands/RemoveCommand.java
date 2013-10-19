package ru.fizteh.fivt.students.vyatkina.database.commands;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.vyatkina.shell.Command;

public class RemoveCommand implements Command {
    Table table;

    public RemoveCommand (Table table) {
        this.table = table;
    }

    @Override
    public void execute (String[] args) {
        String key = args[0];
        String result = table.remove (key);
        if (result == null) {
            System.out.println ("not found");
        }  else {
            System.out.println ("removed");
        }
    }

    @Override
    public String getName () {
        return "remove";
    }

    @Override
    public int getArgumentCount () {
        return 1;
    }
}
