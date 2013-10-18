package ru.fizteh.fivt.students.vyatkina.database.commands;

import ru.fizteh.fivt.students.vyatkina.database.Table;
import ru.fizteh.fivt.students.vyatkina.shell.Command;


public class GetCommand implements Command {

   private final Table table;

   public GetCommand (Table table) {
       this.table = table;
   }

    @Override
    public void execute (String[] args) {
        String key = args [0];
        String result = table.get (key);
        if (result == null) {
            System.out.println ("not found");
        } else {
            System.out.println ("found");
            System.out.println (result);
        }
    }

    @Override
    public String getName () {
        return "get";
    }

    @Override
    public int getArgumentCount () {
        return 1;
    }
}
