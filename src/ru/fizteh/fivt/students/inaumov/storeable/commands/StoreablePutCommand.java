package ru.fizteh.fivt.students.inaumov.storeable.commands;

import ru.fizteh.fivt.students.inaumov.multifilemap.MultiFileMapShellState;
import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;

public class StoreablePutCommand<Table, Key, Value, State extends MultiFileMapShellState<Table, Key, Value>>
        extends AbstractCommand<State> {
    public StoreablePutCommand() {
        super("put", -1);
    }

    public void execute(String[] args, State state) {
        if (args.length <= 2) {
            throw new IllegalArgumentException("error: expected key and values");
        }

        String[] valueArr = new String[args.length - 2];
        for (int i = 2; i < args.length; ++i) {
            valueArr[i - 2] = args[i];
        }

        Key key = state.parseKey(args[1]);

        StringBuilder stringBuilder = new StringBuilder();
        for (final String entry: valueArr) {
            stringBuilder.append(entry);
        }

        System.out.println("try to put value: " + stringBuilder.toString());
        Value value = state.parseValue(stringBuilder.toString());
        Value oldValue = state.put(key, value);

        if (oldValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(state.valueToString(oldValue));
        }
    }
}
