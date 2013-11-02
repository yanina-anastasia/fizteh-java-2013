package ru.fizteh.fivt.students.adanilyak.filemap;

import java.util.NoSuchElementException;

/**
 * User: Alexander
 * Date: 15.10.13
 * Time: 19:36
 */

public enum RequestCommandTypeFileMap {

    put("put"),
    get("get"),
    remove("remove"),
    exit("exit");

    private String typeValue;

    private RequestCommandTypeFileMap(String type) {
        typeValue = type;
    }

    static public RequestCommandTypeFileMap getType(String pType) {
        for (RequestCommandTypeFileMap type : RequestCommandTypeFileMap.values()) {
            if (type.getTypeValue().equals(pType)) {
                return type;
            }
        }
        throw new NoSuchElementException("Unknown command");
    }

    public String getTypeValue() {
        return typeValue;
    }

}