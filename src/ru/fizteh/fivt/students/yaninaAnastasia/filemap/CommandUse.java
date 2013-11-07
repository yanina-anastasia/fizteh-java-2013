package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.students.yaninaAnastasia.shell.Command;

import java.io.IOException;

public class CommandUse extends Command {
    public boolean exec(String[] args, State curState) throws IOException {
        try {
            MultiDBState myState = MultiDBState.class.cast(curState);
            if (args.length != 1) {
                throw new IllegalArgumentException("Illegal arguments");
            }
            String path = myState.getProperty(myState);
            if (!myState.database.tables.containsKey(args[0])) {
                System.out.println(args[0] + " not exists");
                return false;
            }
            if (myState.table != null) {
                myState.table.save();
            }
            if (myState.table != null && myState.table.uncommittedChanges != 0) {
                System.out.println(myState.table.uncommittedChanges + "unsaved changes");
            }
            myState.table = myState.database.getTable(args[0]);
            System.out.println("using " + args[0]);

        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return false;
        } catch (IllegalStateException f) {
            System.err.println(f.getMessage());
            return false;
        }
        return true;
    }

    public String getCmd() {
        return "use";
    }
}
