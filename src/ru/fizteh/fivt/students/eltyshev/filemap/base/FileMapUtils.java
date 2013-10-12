package ru.fizteh.fivt.students.eltyshev.filemap.base;

import java.io.*;
import java.util.ArrayList;

public class FileMapUtils {

    public static byte[] getBytes(String string, String charset)
    {
        byte[] bytes = null;
        try
        {
            bytes = string.getBytes(charset);
        }
        finally
        {
            return bytes;
        }
    }

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

    public static int getByteCount(String string, String charset)
    {
        try
        {
            byte[] bytes = string.getBytes(charset);
            return bytes.length;
        }
        catch(Exception e)
        {
            return 0;
        }
    }
}
