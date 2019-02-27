package org.htsjdk.test;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * All tests should extend from this base class.
 */
public class HtsjdkBaseTest {

    public final Path getDataRootPath() {
        return Paths.get(Paths.get("../data").toUri().normalize().getPath());
    }
}
