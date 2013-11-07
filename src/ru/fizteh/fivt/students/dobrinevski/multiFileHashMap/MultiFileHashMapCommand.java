package ru.fizteh.fivt.students.dobrinevski.multiFileHashMap;

import ru.fizteh.fivt.students.dobrinevski.shell.Command;

public abstract class MultiFileHashMapCommand extends Command {
    MyMultiHashMap parent;

        MultiFileHashMapCommand(int argCount, MyMultiHashMap prnt) {
            super(argCount);
            parent = prnt;
        }
    }
