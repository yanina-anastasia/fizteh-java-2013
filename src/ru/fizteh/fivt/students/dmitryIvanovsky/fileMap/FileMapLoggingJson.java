package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.IdentityHashMap;

public class FileMapLoggingJson {
    Object argument;
    IdentityHashMap<Object, Object> identifyMap = new IdentityHashMap<>();

    FileMapLoggingJson(Object argument) {
        this.argument = argument;
    }

    JSONArray getJSONArray() {
        JSONArray creatingArray = new JSONArray();
        return recursiveLog(argument, creatingArray);
    }

    private JSONArray recursiveLog(Object arg, JSONArray creatingArray) {
        JSONArray newCreatingArray = new JSONArray();
        if (arg == null) {
            creatingArray.put(JSONObject.NULL);
        } else {
            if (Iterable.class.isAssignableFrom(arg.getClass())) {
                if (identifyMap.containsKey(arg)) {
                    creatingArray.put("cyclic");
                } else {
                    identifyMap.put(arg, arg);
                    for (Object obj: (Iterable) arg) {
                        try {
                            newCreatingArray = recursiveLog(obj, newCreatingArray);
                        } catch (java.lang.ClassCastException e) {
                            newCreatingArray.put(arg.toString());
                        }
                    }
                    identifyMap.remove(arg);
                    creatingArray.put(newCreatingArray);
                }
            } else if (arg.getClass().isArray()) {
                if (identifyMap.containsKey(arg)) {
                    creatingArray.put("cyclic");
                } else {
                    identifyMap.put(arg, arg);
                    for (Object obj: (Object[]) arg) {
                        try {
                            newCreatingArray = recursiveLog(obj, newCreatingArray);
                        } catch (java.lang.ClassCastException e) {
                            newCreatingArray.put(obj.toString());
                        }
                    }
                    identifyMap.remove(arg);
                    creatingArray.put(newCreatingArray);
                }
            } else {
                try {
                    JSONArray copy = new JSONArray();
                    for (int i = 0; i < creatingArray.length(); ++i) {
                        copy.put(creatingArray.get(i));
                    }
                    creatingArray.put(arg);
                    if (creatingArray.toString() == null) {
                        creatingArray = copy;
                        creatingArray.put(arg.toString());
                    }
                } catch (java.lang.ClassCastException e) {
                    creatingArray.put(arg.toString());
                }
            }
        }
        return creatingArray;
    }
}
