package ru.fizteh.fivt.students.ermolenko.multifilehashmap;

import ru.fizteh.fivt.students.ermolenko.shell.Command;
import ru.fizteh.fivt.students.ermolenko.shell.Shell;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class CmdUse implements Command {

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
        //или таблица не была выбрана
        if (((MultiFileHashMap) shell).getMultiFileHashMapState().getCurrentTable() != null) {
            if (((MultiFileHashMap) shell).getMultiFileHashMapState().getCurrentTable().getName() != args[0]) {
                File fileForWrite = ((MultiFileHashMapTable) ((MultiFileHashMap) shell).getMultiFileHashMapState().getCurrentTable()).getDataFile();
                Map<String, String> mapForWrite = ((MultiFileHashMapTable) ((MultiFileHashMap) shell).getMultiFileHashMapState().getCurrentTable()).getDataBase();
                MultiFileHashMapUtils.write(fileForWrite, mapForWrite);
            }
        }

        if (((MultiFileHashMap) shell).getMultiFileHashMapState().getTable(args[0]) == null) {
            System.out.println(args[0] + " not exists");
            return;
        }
        ((MultiFileHashMap) shell).getMultiFileHashMapState().setCurrentTable(args[0]);
        System.out.println("using " + args[0]);
    }
}
