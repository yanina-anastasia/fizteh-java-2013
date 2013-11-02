package ru.fizteh.fivt.students.dubovpavel.strings;

import java.util.regex.Pattern;

public class IsKeyAllowable {
    private static Pattern allowable = Pattern.compile("\\w");
    public static boolean check(String key) {
        return allowable.matcher(key).matches();
    }
}
