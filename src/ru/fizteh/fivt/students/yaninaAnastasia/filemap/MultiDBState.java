package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

public class MultiDBState extends DBState {
    Database myDatabase = new Database();
    String curTableName = "";

    public MultiDBState() {

    }

    public String getProperty() {
        String path = System.getProperty("fizteh.db.dir");
        if (path == null) {
            System.err.println("Error with getting property");
            System.exit(1);
        }
        return path;
    }
}
