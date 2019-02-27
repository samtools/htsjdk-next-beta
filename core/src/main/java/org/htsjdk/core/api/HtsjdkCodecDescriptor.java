package org.htsjdk.core.api;

import org.htsjdk.core.api.io.IOResource;
import org.htsjdk.core.utils.PathSpecifier;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

/**
 * Base interface that must be implemented by all htsjdk codec descriptors. Descriptors
 * are lightweight object that are cached by the registry service and used to locate
 * and instantiate a codec for a given input.
 */
public interface HtsjdkCodecDescriptor {

    String getName();

    // Get the minimum number of bytes this codec needs in order to determine whether it can decode a stream.
    int getMinimalFileSignatureSize();

    boolean canDecode(final IOResource resource);

    boolean canDecode(final Path path);

    HtsjdkCodec getCodecInstance(final IOResource resource);

    HtsjdkCodec getCodecInstance(final Path path);

    HtsjdkCodec getCodecInstance(final InputStream is);

    boolean canDecodeSignature(final byte[] streamSignature);

    //Given a pathSpec, resolve any companion sibling files against it (index, .dict, etc.)
    default List<PathSpecifier> resolveSiblings(final PathSpecifier pathSpec) {
        return Collections.singletonList(pathSpec);
    }
}
