package ru.fizteh.fivt.students.dmitryIvanovsky.shell;

import java.io.IOException;
import java.util.Map;

public interface CommandAbstract {
    void exit() throws IOException, Exception;
    String startShellString() throws IOException;
    Map<String, Object[]> mapComamnd();
}
