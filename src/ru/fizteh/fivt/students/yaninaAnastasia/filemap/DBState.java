package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.HashMap;

public class DBState extends State {
    public HashMap<String, String> table = new HashMap<String, String>();
    public RandomAccessFile dbFile;


    DBState() {

    }
}
