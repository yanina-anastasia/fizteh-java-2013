package ru.fizteh.fivt.students.piakovenko.filemap;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 12.10.13
 * Time: 23:25
 * To change this template use File | Settings | File Templates.
 */
public class DataBaseMap {
    private Map<String, String> map = new HashMap<String, String>(15);

    public void put (String key, String value) {
        if (!map.containsKey(key)) {
            map.put(key, value);
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(map.get(key));
            map.remove(key);
            map.put(key, value);
        }
    }

    public void get(String key) {
        if (!map.containsKey(key)) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(map.get(key));
        }
    }

    public void remove (String key) {
        if (!map.containsKey(key)) {
            System.out.println("not found");
        } else {
            map.remove(key);
            System.out.println("removed");
        }
    }

    public void primaryPut (String key, String value) {
            map.put(key, value);
    }

    public Map<String, String> getMap () {
        return map;
    }

}
