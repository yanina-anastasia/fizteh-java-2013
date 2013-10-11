package ru.fizteh.fivt.students.piakovenko.shell;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 01.10.13
 * Time: 20:00
 * To change this template use File | Settings | File Templates.
 */
public class MyException extends Exception {
    private final Exception reason;

    public MyException(Exception e) {
        reason = e;
    }

    public String what() {
        return reason.getMessage();
    }

}
