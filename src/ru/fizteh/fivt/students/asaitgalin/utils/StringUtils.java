package ru.fizteh.fivt.students.asaitgalin.utils;

import java.nio.charset.Charset;
import java.util.Collection;

public class StringUtils {

    public static String join(Iterable<?> objects, String separator) {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (Object o: objects) {
            if (!isFirst) {
                sb.append(separator);
            } else {
                isFirst = false;
            }
            sb.append(o.toString());
        }
        return sb.toString();
    }

    public static String getStringFromArray(Collection<Byte> arrayList, String charsetName) {
        byte[] stringData = new byte[arrayList.size()];
        int counter = 0;
        for (Byte b : arrayList) {
            stringData[counter++] = b.byteValue();
        }
        return new String(stringData, Charset.forName(charsetName));
    }

}
