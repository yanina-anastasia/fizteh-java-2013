package ru.fizteh.fivt.students.fedoseev.filemap;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;
import ru.fizteh.fivt.students.fedoseev.common.AbstractFrame;

import java.io.IOException;

public class GetCommand extends AbstractCommand {
    public GetCommand() {
        super("get", 1);
    }

    @Override
    public void execute(String[] input, AbstractFrame.FrameState state) throws IOException {
        String value = AbstractFileMap.getMap().get(input[0]);

        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found\n" + value);
        }
    }
}
