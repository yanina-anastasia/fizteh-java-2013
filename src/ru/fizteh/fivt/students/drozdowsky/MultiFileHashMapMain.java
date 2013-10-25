package ru.fizteh.fivt.students.drozdowsky;

import ru.fizteh.fivt.students.drozdowsky.utils.Utils;
import ru.fizteh.fivt.students.drozdowsky.modes.ModeController;
import ru.fizteh.fivt.students.drozdowsky.database.MultiFileHashMap;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class MultiFileHashMapMain {

    public static void main(String[] args) {
        String dbDirectory = System.getProperty("fizteh.db.dir");
        if (dbDirectory == null) {
            System.err.println("No database location");
            System.exit(1);
        }

        String[] commandNames = {"create", "drop", "use", "put", "get", "remove", "exit"};
        HashMap<String, Method> map = Utils.getMethods(commandNames, MultiFileHashMap.class);
        try {
            MultiFileHashMap db = new MultiFileHashMap(new File(dbDirectory));
            ModeController<MultiFileHashMap> start = new ModeController<MultiFileHashMap>(db);
            start.execute(map, args);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
