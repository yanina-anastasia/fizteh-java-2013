package ru.fizteh.fivt.students.dubovpavel.filemap;

import java.util.ArrayList;

public class Command {
    String header;
    ArrayList<String> body;
    int index;

    public Command(String header, int index) {
        this.header = header;
        this.index = index;
        body = new ArrayList<String>();
    }

    public void addArgument(String arg) {
        body.add(arg);
    }

    public String getHeader() {
        return header;
    }

    public String getArgument(int index) {
        return body.get(index);
    }

    public int argumentsCount() {
        return body.size();
    }

    public String getDescription() {
        return String.format("Command #%d", index);
    }
}
