package ru.fizteh.fivt.students.musin.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Brother
 * Date: 10.10.13
 * Time: 23:28
 * To change this template use File | Settings | File Templates.
 */
class FileSystemRoutine {
    public static class FileList {
        File file;
        ArrayList<FileList> list;

        public FileList() {
            list = new ArrayList<FileList>();
        }
    }

    public static void deleteDirectoryOrFile(File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                deleteDirectoryOrFile(f);
            }
            file.delete();
        } else
            file.delete();
    }

    public static void getFileList(FileList fl) {
        for (File f : fl.file.listFiles()) {
            FileList files = new FileList();
            files.file = f;
            if (f.isDirectory())
                getFileList(files);
            fl.list.add(files);
        }
    }

    public static void copyFileList(FileList fl, File to) throws IOException {
        try {
            Files.copy(Paths.get(fl.file.getCanonicalPath()), Paths.get(to.getCanonicalPath()), new CopyOption[0]);
        } catch (FileAlreadyExistsException e) {
        }
        for (FileList list : fl.list) {
            File newDir = new File(to.getCanonicalPath() + "/" + list.file.getName());
            copyFileList(list, newDir);
        }
    }

    public static void copyDirectory(File from, File to) throws IOException {
        FileList fl = new FileList();
        fl.file = from;
        getFileList(fl);
        copyFileList(fl, to);
    }
}
