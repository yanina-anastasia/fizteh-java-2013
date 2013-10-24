package ru.fizteh.fivt.students.lizaignatyeva.shell;

import java.io.File;

public class TestCommand extends Command {
    public void run(String[] args){
        for (String c : args) {
            System.out.println(c);
        }
    }
}
