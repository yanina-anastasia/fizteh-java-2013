package ru.fizteh.fivt.students.lizaignatyeva.database;

import ru.fizteh.fivt.students.lizaignatyeva.shell.Command;

public class RemoveCommand extends Command{
    public RemoveCommand() {
        name = "remove";
        argumentsAmount = 1;
    }

    @Override
    public void run(String[] args) throws Exception {
        if (!checkArguments(args)) {
            throw new IllegalArgumentException("invalid usage");
        }
        Database database = DbMain.getCurrentDatabase();
        String key = args[0];
        if (database.data.containsKey(key)) {
            database.data.remove(key);
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
    }
}
