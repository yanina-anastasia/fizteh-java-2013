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

        //если мы работали с одной таблицей, а теперь переключились на другую
        //нужно сохранить изменения
        //или таблица не была выбрана
        if (((MultiFileHashMap) shell).getMultiFileHashMapState().getCurrentTable() != null) {
            if (((MultiFileHashMap) shell).getMultiFileHashMapState().getCurrentTable().getName() != args[0]) {
                File fileForWrite = ((MultiFileHashMapTable) ((MultiFileHashMap) shell).getMultiFileHashMapState().getCurrentTable()).getDataFile();
                Map<String, String> mapForWrite = ((MultiFileHashMapTable) ((MultiFileHashMap) shell).getMultiFileHashMapState().getCurrentTable()).getDataBase();
                MultiFileHashMapUtils.write(fileForWrite, mapForWrite);
            } else {
                return;
            }
        }


        if (((MultiFileHashMap) shell).getMultiFileHashMapState().getTable(args[0]) == null) {
            System.out.println(args[0] + " not exists");
            return;
        }

        Map<String, String> tmpDataBase = ((MultiFileHashMapTable) ((MultiFileHashMap) shell).getMultiFileHashMapState().getTable(args[0])).getDataBase();
        File tmpDataFile = ((MultiFileHashMapTable) ((MultiFileHashMap) shell).getMultiFileHashMapState().getTable(args[0])).getDataFile();
        MultiFileHashMapUtils.read(tmpDataFile, tmpDataBase);
        ((MultiFileHashMap) shell).getMultiFileHashMapState().setCurrentTable(args[0]);

        ((MultiFileHashMap) shell).getMultiFileHashMapState().changeCurrentTable(tmpDataBase, tmpDataFile);

        System.out.println("using " + args[0]);
    }
}
