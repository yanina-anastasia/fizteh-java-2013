package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

public class MultiDBState extends DBState {
    Database myDatabase = new Database();
    String curTableName = "";

    public MultiDBState() {

    }

    public String getProperty(MultiDBState myState) {
        String path = System.getProperty("fizteh.db.dir");
        if (path == null) {
            MultiFileMapUtils saver = new MultiFileMapUtils();
            if (!saver.save(myState)) {
                System.err.println("Previous file was not saved");
            }
            System.err.println("Error with getting property");
            System.exit(1);
        }
        return path;
    }
}
