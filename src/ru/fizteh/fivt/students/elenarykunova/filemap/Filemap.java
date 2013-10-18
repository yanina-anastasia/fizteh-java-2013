package ru.fizteh.fivt.students.elenarykunova.filemap;

public class Filemap {
    public static void main(String[] args) {
        DataBase db = new DataBase("db.dat");
        ExecuteCmd my = new ExecuteCmd(db);
        my.workWithUser(args);
        db.commitChanges();
    }
}
