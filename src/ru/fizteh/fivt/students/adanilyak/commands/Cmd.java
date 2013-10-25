package ru.fizteh.fivt.students.adanilyak.commands;

import java.util.Vector;

/**
 * User: Alexander
 * Date: 20.10.13
 * Time: 22:39
 */
public interface Cmd {
    String getName();

    int getAmArgs();

    void work(Vector<String> args) throws Exception;
}
