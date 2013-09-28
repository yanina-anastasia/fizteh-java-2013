package ru.fizteh.fivt.students.vyatkina.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created with IntelliJ IDEA.
 * User: ava_katushka
 * Date: 23.09.13
 * Time: 22:01
 * To change this template use File | Settings | File Templates.
 */
public class FileUtils {
    public static void copyFile (File in, File out) throws IOException {
        if (in.isDirectory ()) {
            File [] innerFiles = in.listFiles();
            for (File f: innerFiles)
                copyFile (f,out);
        }
        else {
        FileChannel inChannel = new FileInputStream (in).getChannel ();
        FileChannel outChannel = new FileOutputStream  (out).getChannel ();

        try {
            inChannel.transferTo (0,inChannel.size (),outChannel);
        }
        catch (IOException e) {
            throw e;
        }
        finally {
            if (inChannel != null) {
                inChannel.close ();
            }
            if (outChannel != null) {
                outChannel.close ();
            }
        }
        }

    }
}
