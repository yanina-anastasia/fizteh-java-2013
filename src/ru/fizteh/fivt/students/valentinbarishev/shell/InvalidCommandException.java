package ru.fizteh.fivt.students.valentinbarishev.shell;

/**
 * Created with IntelliJ IDEA.
 * User: Valik
 * Date: 03.10.13
 * Time: 0:52
 * To change this template use File | Settings | File Templates.
 */
public class InvalidCommandException extends Error {
     public InvalidCommandException(String message) {
        super("Invalid command: " + message);
     }
}
