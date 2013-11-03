package ru.fizteh.fivt.students.adanilyak.commands;

import ru.fizteh.fivt.students.adanilyak.multifilehashmap.DataBaseGlobalState;
import ru.fizteh.fivt.students.adanilyak.multifilehashmap.TableStorage;

import java.io.IOException;
import java.util.List;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 14:19
 */
public class CmdUse implements Cmd {
    private final String name = "use";
    private final int amArgs = 1;
    private DataBaseGlobalState workState;

    public CmdUse(DataBaseGlobalState dataBaseState) {
        workState = dataBaseState;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getAmArgs() {
        return amArgs;
    }

    @Override
    public void work(List<String> args) throws IOException {
        String useTableName = args.get(1);
        if (workState.getTable(useTableName) == null) {
            System.err.println(useTableName + " not exists");
        } else {
            if (workState.getCurrentTable() != null) {
                int amChanges = ((TableStorage)workState.currentTable).getAmountOfChanges();
                if (amChanges != 0) {
                    System.err.println(amChanges + " unsaved changes");
                    return;
                }
            }
            workState.setCurrentTable(useTableName);
            System.out.println("using " + useTableName);
        }
    }
}
