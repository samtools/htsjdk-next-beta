package org.htsjdk.core.impl.bam;

import org.htsjdk.core.api.HtsjdkCodec;
import org.htsjdk.core.api.io.IOResource;
import org.htsjdk.core.utils.Version;

import java.io.InputStream;
import java.nio.file.Path;

/**
 * Mock, do-nothing BAM codec used to exercise the reader factory infrastructure
 */
public class BAMCodec implements HtsjdkCodec {

    public BAMCodec(final IOResource ioResource) { this(ioResource.toPath()); }

    public BAMCodec(final Path pathResource) {
        // TODO
    }

    public BAMCodec(final InputStream inputStream) {

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

    @Override
    public boolean runVersionUpgrade(final Version sourceVersion, final Version targetVersion) {
        return false;
    }
}
