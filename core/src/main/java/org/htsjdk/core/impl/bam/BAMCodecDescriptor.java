package org.htsjdk.core.impl.bam;

import org.htsjdk.core.api.HtsjdkCodecDescriptor;
import org.htsjdk.core.api.HtsjdkCodec;
import org.htsjdk.core.api.io.IOResource;
import org.htsjdk.core.exception.HtsjdkIOException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Mock, do-nothing BAM codec descriptor.
 */
public class BAMCodecDescriptor implements HtsjdkCodecDescriptor {

    public static final String BAM_FILE_EXTENSION = ".bam";
    public static final String BAM_MAGIC = "BAM\1";

    @Override
    public String getName() {
        return "BAM codec descriptor";
    }

    @Override
    public int getMinimalFileSignatureSize() {
        return BAM_MAGIC.length();
    }

    @Override
    public boolean canDecode(final IOResource resource) {
        return resource.getURIString().endsWith(BAM_FILE_EXTENSION);
    }

    @Override
    public boolean canDecode(final Path path) {
        return path.endsWith(BAM_FILE_EXTENSION);
    }

    // uses a byte array rather than a stream to reduce the need to repeatedly mark/reset the
    // stream for each codec
    @Override
    public boolean canDecodeSignature(final byte[] signatureBytes) {
        return signatureBytes.equals("BAM");
    }

    @Override
    public HtsjdkCodec getCodecInstance(final IOResource ioResource) {
        return getCodecInstance(ioResource.toPath());
    }

    @Override
    public HtsjdkCodec getCodecInstance(final Path resourcePath) {
        try {
            return new BAMCodec(Files.newInputStream(resourcePath));
        } catch (IOException e) {
            throw new HtsjdkIOException(e);
        }
    }

    @Override
    public HtsjdkCodec getCodecInstance(final InputStream is) {
        return new BAMCodec(is);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
