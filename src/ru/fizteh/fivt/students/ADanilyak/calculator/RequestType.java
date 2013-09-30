package ru.fizteh.fivt.students.adanilyak.calculator;

/**
 * User: Alexander
 * Date: 29.09.13
 * Time: 12:34
 */

import java.util.regex.Pattern;

public enum RequestType {

    PLUS("+"),
    MINUS("-"),
    MULT("*"),
    DIV("/"),
    OBRCKT("("),
    CBRCKT(")");

    private String typeValue;

    private RequestType(String type) {
        typeValue = type;
    }

    static public RequestType getType(String pType) {
        for (RequestType type : RequestType.values()) {
            if (type.getTypeValue().equals(pType)) {
                return type;
            }
        }
        throw new RuntimeException("unknown type");
    }

    public String getTypeValue() {
        return typeValue;
    }

}
