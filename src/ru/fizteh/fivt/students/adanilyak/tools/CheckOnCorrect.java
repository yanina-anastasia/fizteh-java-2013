package ru.fizteh.fivt.students.adanilyak.tools;

/**
 * User: Alexander
 * Date: 01.11.13
 * Time: 21:55
 */
public class CheckOnCorrect {
    public static boolean goodName(String givenName) {
        if (givenName == null) {
            return false;
        }
        if (givenName.matches("[a-zA-Zа-яА-Я0-9]+")) {
            return true;
        } else {
            return false;
        }
    }
}
