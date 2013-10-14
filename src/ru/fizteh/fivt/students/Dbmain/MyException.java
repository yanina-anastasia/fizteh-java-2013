package ru.fizteh.fivt.students.piakovenko.Dbmain;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 01.10.13
 * Time: 20:00
 * To change this template use File | Settings | File Templates.
 */
public class MyException extends Exception {
    private final Throwable reason;

    public MyException(Throwable e) {
        reason = e;
    }

    public String what() {
        return reason.getMessage();
    }

}
