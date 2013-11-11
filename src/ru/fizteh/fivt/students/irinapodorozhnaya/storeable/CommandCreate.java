package ru.fizteh.fivt.students.irinapodorozhnaya.storeable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.fizteh.fivt.students.irinapodorozhnaya.shell.AbstractCommand;
import ru.fizteh.fivt.students.irinapodorozhnaya.utils.Utils;

public class CommandCreate extends AbstractCommand {
    
    private StoreableState state;
    
    public CommandCreate(StoreableState state) {
        super(-1);
        this.state = state;
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public void execute(String[] args) throws IOException {
        try {
            state.create(args[1], parser(args));
        } catch (IllegalArgumentException e) {
            throw new IOException("create: " + e.getMessage());
        }
        state.getOutputStream().println("created");
    }
    
    public static List<Class<?>> parser(String[] args) throws IOException {
        if (args.length < 3) {
            throw new IOException("create: too few arguments");
        }

        String first = args[2];
        String last = args[args.length - 1];

        if (!first.startsWith("(") || !last.endsWith(")")) {
            throw new IOException("create: Input not matchs \"create tablename (type1 ... typeN)\"");
        }
        
        List<Class<?>> columnType = new ArrayList<>();
        
        first = (first.length() > 1) ? first.substring(1) : null;
        last = (last.length() > 1) ? last.substring(0, last.length() - 1) : null;

        columnType.add(Utils.detectClass(first));
        for (int i = 3; i < args.length - 1; ++i) {
            columnType.add(Utils.detectClass(args[i]));
        }
        columnType.add(Utils.detectClass(last));
        return columnType;
    }
}
