package ru.fizteh.fivt.students.inaumov.filemap.base;

public enum TableState {
    NOT_INIT {
        @Override
        public void checkAvailable() {
            throw new IllegalStateException("table is not init yet");
        }
    },
    WORKING {
        @Override
        public void checkAvailable() {
            //
        }
    },
    CLOSED {
        @Override
        public void checkAvailable() {
            throw new IllegalStateException("table is already closed");
        }
    };

    public abstract void checkAvailable();
}
