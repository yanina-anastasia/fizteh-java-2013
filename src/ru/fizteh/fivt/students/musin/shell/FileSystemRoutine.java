package ru.fizteh.fivt.students.musin.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

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
        }
        if (!file.delete()) {
            System.err.printf("rm: file '%s' can't be deleted\n");
        }
    }

    public static void getFileList(FileList fl) {
        if (fl.file.isDirectory()) {
            for (File f : fl.file.listFiles()) {
                FileList files = new FileList();
                files.file = f;
                if (f.isDirectory()) {
                    getFileList(files);
                }
                fl.list.add(files);
            }
        }
    }

    public static void copyFileList(FileList fl, File to) throws IOException {
        try {
            Files.copy(Paths.get(fl.file.getCanonicalPath()), Paths.get(to.getCanonicalPath()), new CopyOption[0]);
        } catch (FileAlreadyExistsException e) {
            //It Already there, no action needed
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
