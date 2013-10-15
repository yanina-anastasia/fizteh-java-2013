package ru.fizteh.fivt.students.eltyshev.filemap.base;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class FileMapUtils {

    public static byte[] toByteArray(ArrayList<Byte> bytes)
    {
        byte[] result = new byte[bytes.size()];
        for(int index = 0; index < bytes.size(); ++index)
        {
            result[index] = bytes.get(index);
        }
        return result;
    }

    public static boolean checkFileExists(String path)
    {
        File file = new File(path);
        return file.exists();
    }

    public static int getByteCount(String string, Charset charset)
    {
        return string.getBytes(charset).length;
    }
}
