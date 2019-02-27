package org.htsjdk.core.utils;

import org.htsjdk.core.api.HtsjdkCodec;
import org.htsjdk.core.api.HtsjdkCodecDescriptor;
import org.htsjdk.core.api.io.IOResource;
import org.htsjdk.core.exception.HtsjdkIOException;
import org.htsjdk.core.exception.HtsjdkPluginException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Registry/cache for codec descriptors for discovered codecs.
 */
public class CodecRegistry {
    private static final CodecRegistry codecRegistry = new CodecRegistry();

    private static List<HtsjdkCodecDescriptor> discoveredCodecs = new ArrayList<>();

    // minimum number of bytes required to allow any codec to deterministically decide if it can
    // decode a stream
    private static int minSignatureSize = 0;

    static {
        CodecDiscoveryService.discoverCodecs().forEach(codecRegistry::addCodecDescriptor);
    }

    /**
     * Add a codec to the registry
     */
    private void addCodecDescriptor(final HtsjdkCodecDescriptor codecDescriptor) {
        discoveredCodecs.add(codecDescriptor);
        final int minSignatureBytesRequired = codecDescriptor.getMinimalFileSignatureSize();
        if (minSignatureBytesRequired < 1) {
            throw new HtsjdkPluginException(
                    String.format("%s: getMinimalFileSignatureSize must be > 0", codecDescriptor.getName())
            );
        }
        minSignatureSize = Integer.max(minSignatureSize, minSignatureBytesRequired);
    }

    // TODO: this should have a name and contract that reflects that its only looking at the URI
    // Once we find a codec, hand it off already primed with the version header, etc).
    public static HtsjdkCodec findCodecFor(final IOResource inputResource) {
        final Optional<HtsjdkCodecDescriptor> codec =
                discoveredCodecs.stream()
                    .filter(codecDescriptor -> codecDescriptor.canDecode(inputResource))
                    .findFirst();

        if (codec.isPresent()) {
            return codec.get().getCodecInstance(inputResource);
        } else {
            //TODO: who closes/owns the lifetime of this input stream ?
            InputStream is = inputResource.getInputStream();
            return findCodecFor(inputResource.toString(), is);
        }
    }

    // Once we find a codec, hand it off already primed with the version header, etc).
    public static HtsjdkCodec findCodecFor(final String sourceName, final InputStream is) {
        final byte[] signatureBytes = new byte[minSignatureSize];
        try {
            final int numRead = is.read(signatureBytes);
            if (numRead <= 0) {
                throw new HtsjdkIOException(String.format("Failure reading content from stream for %s", sourceName));
            }
            return discoveredCodecs.stream()
                    .filter(
                        // its possible that the input is a legitimate stream for some codec, but
                        // contains less bytes than are required even for signature detection by another
                        // codecs, so skip any descriptors that require more bytes than are available
                        codecDescriptor ->
                                numRead >= codecDescriptor.getMinimalFileSignatureSize() &&
                                codecDescriptor.canDecodeSignature(signatureBytes))
                    .findFirst()
                    .orElseThrow(() -> new HtsjdkIOException(String.format("No codec found for %s", sourceName)))
                    .getCodecInstance(is);
        } catch (IOException e) {
            throw new HtsjdkIOException(String.format("Failure reading signature from stream for %s", sourceName), e);
        }
    }
}
