package ru.fizteh.fivt.students.eltyshev.multifilemap;

import ru.fizteh.fivt.students.eltyshev.filemap.base.FilemapWriter;
import ru.fizteh.fivt.students.eltyshev.filemap.base.TableBuilder;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class DistributedSaver {
    static final int BUCKET_COUNT = 16;
    static final int FILES_PER_DIR = 16;

    public static void save(TableBuilder builder, Set<DatabaseFileDescriptor> changedFiles) throws IOException {
        File tableDirectory = builder.getTableDirectory();

        for (DatabaseFileDescriptor descriptor : changedFiles) {
            Set<String> keysToSave = getKeysToSave(builder, descriptor);
            String bucketName = String.format("%d.dir", descriptor.bucket);
            String fileName = String.format("%d.dat", descriptor.file);
            File bucketDirectory = new File(tableDirectory, bucketName);

            if (isBucketEmpty) {
                MultifileMapUtils.deleteFile(bucketDirectory);
                continue;
            }

            for (int fileNumber = 0; fileNumber < FILES_PER_DIR; ++fileNumber) {
                String fileName = String.format("%d.dat", fileNumber);
                File file = new File(bucketDirectory, fileName);
                if (keysToSave.get(fileNumber).isEmpty()) {
                    MultifileMapUtils.deleteFile(file);
                    continue;
                }
                if (!bucketDirectory.exists()) {
                    bucketDirectory.mkdir();
                }
                FilemapWriter.saveToFile(file.getAbsolutePath(), keysToSave, builder);
            }
        }
        cleanTableDirectory(builder);
    }
}
