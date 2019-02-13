package org.htsjdk.core.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class IOUtils {

    /**
     * Create a temporary file using a given name prefix and name suffix and return a {@link java.nio.file.Path}.
     * @param prefix
     * @param suffix
     * @return temp File that will be deleted on exit
     * @throws IOException
     */
    public static Path createTempPath(final String prefix, final String suffix) throws IOException {
        final File tempFile = File.createTempFile(prefix, suffix);
        tempFile.deleteOnExit();
        return tempFile.toPath();
    }

}
