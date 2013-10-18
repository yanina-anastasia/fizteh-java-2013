package ru.fizteh.fivt.students.fedoseev.filemap;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;
import ru.fizteh.fivt.students.fedoseev.common.AbstractFrame;

import java.io.IOException;

public class RemoveCommand extends AbstractCommand {
    public RemoveCommand() {
        super("remove", 1);
    }

    @Override
    public void execute(String[] input, AbstractFrame.FrameState state) throws IOException {
        String key = AbstractFileMap.getMap().remove(input[0]);

        if (key == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }
}
