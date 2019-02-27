package org.htsjdk.core.utils;

import org.htsjdk.core.api.HtsjdkCodecDescriptor;
import org.htsjdk.core.utils.CodecDiscoveryService;
import org.htsjdk.test.HtsjdkBaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class CodecDiscoveryServiceTest extends HtsjdkBaseTest {

    @Test
    public void testDescriptorServiceLoader() {
        final List<HtsjdkCodecDescriptor> descriptors = CodecDiscoveryService.discoverCodecs();
        Assert.assertFalse(descriptors.isEmpty());
    }
}
