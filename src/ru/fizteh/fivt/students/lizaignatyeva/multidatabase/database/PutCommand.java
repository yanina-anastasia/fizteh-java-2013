package ru.fizteh.fivt.students.lizaignatyeva.multidatabase.database;

import ru.fizteh.fivt.students.lizaignatyeva.shell.Command;

public class PutCommand extends Command {
    public PutCommand() {
        name = "put";
        argumentsAmount = 2;
    }

    @Override
    public void run(String[] args) throws Exception {
        if (!checkArguments(args)) {
            throw new IllegalArgumentException("invalid usage");
        }

        Database database = DbMain.getCurrentDatabase();
        //System.err.println("HERE");
        String key = args[0];
        String value = args[1];

        if (database.data.containsKey(key)) {
            System.out.println("overwrite");
            System.out.println(database.data.get(key));
            database.data.remove(key);
            database.data.put(key, value);
        } else {
            System.out.println("new");
            database.data.put(key, value);
        }

    }
}
