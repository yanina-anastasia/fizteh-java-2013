package ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.irinaGoltsman.shell.Code;

import java.io.File;
import java.util.HashMap;

public class DBTable implements Table {

    private File tableDirectory;
    private HashMap<String, String> tableStorage = new HashMap<>();
    private int numberOfChangedValues = 0;

    public DBTable(File dataDirectory) throws Exception {
        tableDirectory = dataDirectory;
        Code returnCOde = FileManager.readDBFromDisk(tableDirectory, tableStorage);
        if (returnCOde != Code.OK) {
            throw new Exception("Error while reading table: " + this.getName());
        }
    }

    @Override
    public String getName() {
        return tableDirectory.getName();
    }

    @Override
    public String get(String key) {
        return tableStorage.get(key);
    }

    @Override
    public String put(String key, String value) {
        return tableStorage.put(key, value);
    }

    @Override
    public String remove(String key) {
        return tableStorage.remove(key);
    }

    @Override
    public int size() {
        return tableStorage.size();
    }

    //@return Количество сохранённых ключей. Вывод - число измененных значений:
    @Override
    public int commit() {
        if (FileManager.writeTableOnDisk(tableDirectory, tableStorage) != Code.OK) {
            return -1;
        }
        return size();
    }

    //Ещё не написанная функция.
    @Override
    public int rollback() {
        return 0;
    }
}
