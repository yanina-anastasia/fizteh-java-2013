package ru.fizteh.fivt.students.inaumov.filemap;

public interface FileMapShellState<Table, Key, Value> {
    public Value put(Key key, Value value);

    public Value get(Key key);

    public Value remove(Key key);

    public int size();

    public int commit();

    public int rollback();

    public Table getTable();

    public String keyToString(Key key);

    public String valueToString(Value value);

    public Key parseKey(String key);

    public Value parseValue(String value);
}
