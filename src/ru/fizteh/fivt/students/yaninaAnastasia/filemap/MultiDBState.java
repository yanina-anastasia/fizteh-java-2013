package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

public class MultiDBState extends DBState {
    public DatabaseTableProvider database;
    String curTableName = "";

    public MultiDBState() {

    }

    public String getProperty(MultiDBState myState) {
        String path = System.getProperty("fizteh.db.dir");
        if (path == null) {
            if (!table.save()) {
                System.err.println("Previous file was not saved");
            }
            System.err.println("Error with getting property");
            System.exit(1);
        }
        return path;
    }
}
