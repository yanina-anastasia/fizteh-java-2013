package ru.fizteh.fivt.students.drozdowsky.filemap;

public class FilemapUtils {

    public static boolean executeCommand(Database db, String[] args) {
        if (args.length == 0) {
            return true;
        }
        String command = args[0];
        if (command.equals("put")) {
            db.put(args);
        } else if (command.equals("get")) {
            db.get(args);
        } else if (command.equals("remove")) {
            db.remove(args);
        } else if (command.equals("exit")) {
            db.close();
            System.exit(0);
        } else {
            System.err.println(args[0] + ": command not found");
            return false;
        }
        return true;
    }
}
