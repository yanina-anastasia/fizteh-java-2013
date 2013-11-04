package ru.fizteh.fivt.students.adanilyak.filemap;

/**
 * User: Alexander
 * Date: 15.10.13
 * Time: 19:34
 */

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Shell {
    private Map<String, String> state;
    private File dataBaseFileName;
    private boolean typeOfRunning;

    public Shell(String fileName) {
        state = new HashMap<String, String>();
        dataBaseFileName = new File(System.getProperty("fizteh.db.dir"), fileName);
        try {
            WorkWithBytes.readIntoMap(dataBaseFileName, state);
        } catch (Exception exc) {
            System.err.println(exc.getMessage());
        }
    }

    public void setInteractiveType() {
        typeOfRunning = false;
    }

    public void setPackageType() {
        typeOfRunning = true;
    }

    public boolean getTypeOfRunning() {
        return typeOfRunning;
    }

    public String put(String key, String value) {
        return state.put(key, value);
    }

    public String get(String key) {
        return state.get(key);
    }

    public String remove(String key) {
        return state.remove(key);
    }

    public void testPrint() {
        System.out.println(state);
    }

    public void exit() throws Exception {
        WorkWithBytes.writeIntoFile(dataBaseFileName, state);
    }
}
