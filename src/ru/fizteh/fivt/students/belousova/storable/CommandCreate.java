package ru.fizteh.fivt.students.belousova.storable;

import ru.fizteh.fivt.students.belousova.shell.Command;
import ru.fizteh.fivt.students.belousova.utils.StorableUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandCreate implements Command {
    StorableShellState state = null;

    public CommandCreate(StorableShellState state) {
        this.state = state;
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public void execute(String[] args) throws IOException {
        if (state.getTable(args[1]) != null) {
            System.out.println(args[1] + " exists");
        } else {
            List<Class<?>> columnTypes = new ArrayList<>();
            args[2] = args[2].trim();
            if (!args[2].startsWith("(") || !args[2].endsWith(")")) {
                throw new IOException("wrong create format");
            }
            args[2] = args[2].substring(1, args[2].length() - 1);
            String[] types = args[2].trim().split("\\s+");
            for (String type : types) {
                columnTypes.add(StorableUtils.convertStringToClass(type));
            }
            state.createTable(args[1], columnTypes);
            System.out.println("created");
        }
    }

    @Override
    public int getArgCount() {
        return 2;
    }

}
