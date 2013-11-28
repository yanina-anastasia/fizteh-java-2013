package ru.fizteh.fivt.students.surakshina.filemap;

import java.util.concurrent.atomic.AtomicBoolean;

public class CloseState {
    AtomicBoolean isClosed = new AtomicBoolean(false);

    public CloseState() {
    }

    public void checkClosed() {
        if (isClosed.get()) {
            throw new IllegalStateException("It has closed already");
        }
    }

    public void setClose() {
        isClosed.set(true);
    }

}
