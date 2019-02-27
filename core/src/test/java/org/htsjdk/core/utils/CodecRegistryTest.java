package org.htsjdk.core.utils;

import org.htsjdk.core.api.HtsjdkCodec;
import org.htsjdk.core.api.io.IOResource;
import org.htsjdk.test.HtsjdkBaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Paths;

public class CodecRegistryTest extends HtsjdkBaseTest {

    @Test
    public void testFindCodec() {
        final IOResource ioResource = new PathSpecifier(getDataRootPath().resolve(Paths.get("simple.bam")).toString());
        HtsjdkCodec codec = CodecRegistry.findCodecFor(ioResource);
        Assert.assertNotNull(codec);
    }
}
