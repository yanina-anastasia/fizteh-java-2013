package ru.fizteh.fivt.students.piakovenko.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 01.10.13
 * Time: 19:41
 * To change this template use File | Settings | File Templates.
 */
public class CurrentStatus {
    private String currentDirectory;

    CurrentStatus() throws IOException{
        File f = new File("");
        currentDirectory =  f.getCanonicalPath();

    }

    public String getCurrentDirectory() {
        return currentDirectory;
    }

    public void copy(File from, File to) throws MyException {
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            inStream = new FileInputStream(from);
            outStream = new FileOutputStream(to);
            byte[] buffer = new byte[4096];
            int read = 0;
            while ((read = inStream.read(buffer)) > 0) {
                outStream.write(buffer, 0, read);
            }
        } catch (IOException e) {
            throw new MyException("Error: " + e.getMessage());
        } finally {
            try {
                inStream.close();
                outStream.close();
            } catch (IOException e) {
                throw new MyException("Error: " +e.getMessage());
            }
        }
    }

    public void copyRecursively (File from, File to) throws MyException, IOException {
        if (from.isDirectory()){
            File fromNew = new File(to.getCanonicalPath() + File.separator + from.getName());
            if (!fromNew.mkdirs()){
                throw new MyException("Unable to create this directory - " + fromNew.getCanonicalPath());
            }
            for (File f: from.listFiles()){
                copyRecursively(f, fromNew);
            }
            return;
        }
        to = new File (to.getCanonicalPath() + File.separator + from.getName());
        if (!to.exists()) {
            to.createNewFile();
        }
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            inStream = new FileInputStream(from);
            outStream = new FileOutputStream(to);
            byte[] buffer = new byte[4096];
            int read = 0;
            while ((read = inStream.read(buffer)) > 0) {
                outStream.write(buffer, 0, read);
            }
        } catch (IOException e) {
            throw new MyException("Error: " + e.getMessage());
        } finally {
            try {
                inStream.close();
                outStream.close();
            } catch (IOException e) {
                throw new MyException("Error: " +e.getMessage());
            }
        }
    }

    public void changeCurrentDirectory(String s) {
        currentDirectory = s;
    }
    CurrentStatus(String s) throws IOException{
        File f = new File(s);
        currentDirectory =  f.getCanonicalPath();
    }

}
