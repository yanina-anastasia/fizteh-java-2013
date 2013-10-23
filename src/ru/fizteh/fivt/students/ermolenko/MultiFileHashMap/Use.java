package ru.fizteh.fivt.students.ermolenko.MultiFileHashMap;

import ru.fizteh.fivt.students.ermolenko.shell.Command;
import ru.fizteh.fivt.students.ermolenko.shell.Shell;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Use implements Command {

    @Override
    public String getName() {

        return "use";
    }

    @Override
    public void executeCmd(Shell shell, String[] args) throws IOException {
        /*
        if (((MFHM)shell).getMFHMState().getCurrentTable() == null) {
            System.out.println("no table");
            return;
        }
        */

        //если мы работали с одной таблицей, а теперь переключились на другую
        //нужно сохранить изменения
        if (((MFHM) shell).getMFHMState().getFlag() == 1) {
            File fileForWrite = ((MFHMTable) ((MFHM) shell).getMFHMState().getCurrentTable()).getDataFile();
            Map<String, String> mapForWrite = ((MFHMTable) ((MFHM) shell).getMFHMState().getCurrentTable()).getDataBase();
            MFHMUtils.write(fileForWrite, mapForWrite);
        }

        if (((MFHM) shell).getMFHMState().getFlag() == 0) {
            ((MFHM) shell).getMFHMState().changeFlag();
        }

        if (((MFHM) shell).getMFHMState().getTable(args[0]) == null) {
            System.out.println(args[0] + " not exists");
            return;
        }
        ((MFHM) shell).getMFHMState().setCurrentTable(args[0]);
        System.out.println("using " + args[0]);
    }
}
