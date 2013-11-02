package ru.fizteh.fivt.students.drozdowsky;

import ru.fizteh.fivt.students.drozdowsky.Commands.MFHMController;
import ru.fizteh.fivt.students.drozdowsky.utils.Utils;
import ru.fizteh.fivt.students.drozdowsky.modes.ModeController;
import ru.fizteh.fivt.students.drozdowsky.database.MultiFileHashMap;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;

public class MultiFileHashMapMain {

    public static void main(String[] args) {
        String dbDirectory = System.getProperty("fizteh.db.dir");
        if (dbDirectory == null) {
            System.err.println("No database location");
            System.exit(1);
        }
        if (!new File(dbDirectory).isDirectory()) {
            System.err.println(dbDirectory + ": not a directory");
            System.exit(1);
        }

        String[] commandNames = {"create", "drop", "use", "put", "get", "remove", "exit", "size", "commit", "rollback"};
        HashMap<String, Method> map = Utils.getMethods(commandNames, MFHMController.class);
        MFHMController db = new MFHMController(new MultiFileHashMap(dbDirectory));
        ModeController<MFHMController> start = new ModeController<>(db);
        start.execute(map, args);
    }
}
