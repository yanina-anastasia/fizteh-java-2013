package ru.fizteh.fivt.students.dmitryIvanovsky.shell;

import java.io.IOException;
import java.util.Map;

public interface CommandAbstract {
    void exit() throws IOException;
    String startShellString() throws IOException;
    //boolean selfParsing();
    Map<String, String> mapComamnd();
    Map<String, Boolean> mapSelfParsing();
}
