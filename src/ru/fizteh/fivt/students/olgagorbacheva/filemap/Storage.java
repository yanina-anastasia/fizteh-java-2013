package ru.fizteh.fivt.students.olgagorbacheva.filemap;

import java.util.HashMap;
import java.util.Map;

public class Storage {
      Map<String, String> storage;

      public Storage() {
            storage = new HashMap<String, String>();
      }

      public boolean put(String key, String value) {
            if (storage.get(key) != null) {
                  return false;
            }
            storage.put(key, value);
            return true;
      }

      public boolean set(String key, String value) {
            if (storage.get(key) == null) {
                  return false;
            }
            storage.put(key, value);
            return true;
      }

      public boolean remove(String key) {
            if (storage.get(key) == null) {
                  return false;
            }
            storage.remove(key);
            return true;
      }

      public String get(String key) {
            if (storage.get(key) == null) {
                  return null;
            }
            return storage.get(key);
      }

      public Map<String, String> getMap() {
            return storage;
      }
      
      public int getSize() {
            return storage.size();
      }
      
      public void clear() {
            storage.clear();
      }
      
//      public ... getKeys() {
//            return storage.
//      }

}
