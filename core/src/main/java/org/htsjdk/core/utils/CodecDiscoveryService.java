package org.htsjdk.core.utils;

import org.htsjdk.core.api.HtsjdkCodecDescriptor;

import java.util.*;

/**
 * Service loader for dynamically discovering htsjdk codecs.
 */
public class CodecDiscoveryService {
    private static ServiceLoader<HtsjdkCodecDescriptor> serviceLoader = ServiceLoader.load(HtsjdkCodecDescriptor.class);

    private CodecDiscoveryService() {}

    public static List<HtsjdkCodecDescriptor> discoverCodecs() {
        final List<HtsjdkCodecDescriptor> descriptors = new ArrayList<>();

        final Iterator<HtsjdkCodecDescriptor> descriptorIterator = serviceLoader.iterator();
        while (descriptorIterator.hasNext()) {
            descriptors.add(descriptorIterator.next());
        }
        return descriptors;
    }
}