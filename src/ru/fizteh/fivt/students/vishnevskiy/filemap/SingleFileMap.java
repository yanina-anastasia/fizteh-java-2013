package ru.fizteh.fivt.students.vishnevskiy.filemap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SingleFileMap {
    private Map<String, String> map = new HashMap<String, String>();
    private File datebase;

    public SingleFileMap(File datebase) {
        try {
            this.datebase = datebase;
            FileReader reader = new FileReader(datebase);
            reader.readFile(map);
        } catch (FileNotFoundException e) {
            // do nothing
        } catch (IOException e) {
            System.err.println("Failed to read datebase file");
            System.exit(1);
        }
    }

    public String get(String key) {
        return map.get(key);
    }

    public String put(String key, String value) {
        String status;
        status = (map.get(key) == null)? null : map.get(key);
        map.put(key, value);
        return status;
    }

    public int remove(String key) {
        int status;
        status = (map.get(key) == null)? 0 : 1;
        map.remove(key);
        return status;
    }


    public void write() {
        try {
            FileWriter writer = new FileWriter(datebase);
            writer.writeFile(map);
        } catch (FileNotFoundException e) {
            System.err.println("Datebase file not found");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Failed to rewrite datebase file");
            System.exit(1);
        }

    }

}
