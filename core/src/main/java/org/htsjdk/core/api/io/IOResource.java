package org.htsjdk.core.api.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Interface representing htsjdk-next input/output resources.
 */
public interface IOResource {

    /**
     * Determine if this resource has a scheme that has an installed NIO file system provider. This does not
     * guarantee the resource can be converted into a {@code java.nio.file.Path}, since the resource can be
     * syntactically valid, and specify a valid file system provider, but still fail to be semantically meaningful.
     * @return true if this URI has a scheme that has an installed NIO file system provider.
     */
    boolean isNIO();

    /**
     * Return true if this {code IOResource} can be resolved to an NIO Path. If true, {@code #toPath()} can be
     * safely called.
     *
     * There are cases where a valid URI with a valid scheme backed by an installed NIO File System
     * still can't be turned into a {@code java.nio.file.Path}, i.e., the following specifies an invalid
     * authority "namenode":
     *
     *  file://namenode/to/file
     *
     * @return {@code true} if this {@code IOResource} can be resolved to an NIO Path.
     */
    boolean isPath();

    /**
     * Get a {@code java.net.URI} object for this {@code IOResource}. Will not be null.
     * @return The {@code URI} object for this IOResource.
     */
    URI getURI();

    /**
     * Returns the String representation of the {{@code UR} backing this {@code IOResource} URI. This string
     * may differ from the normalized string returned from a Path that has been object resolved from this
     * IOResource.
     *
     * @return String from which this URI as originally created. Will not be null, and will always
     * include a URI scheme.
     */
    default String getURIString() { return getURI().toString(); }

    /**
     * Return the raw (source) input used to create this {@code IOResource} as a String.
     */
    String getRawInputString();

    /**
     * Resolve this {@code IOResource} to an NIO Path. Can be safely called only if {@link #isPath()} returns true.
     */
    Path toPath();

    /**
     * Return a string message describing why this IOResource cannot be converted to a {@code java.nio.file.Path}
     * ({@code #isPath()} returns false).
     *
     * @return Optional<String></String> message explaining toPath failure reason, since it can fail for various reasons.
     */
    Optional<String> getToPathFailureReason();

    /**
     * Return the scheme for this IOResource. For file resources (URIs that have no explicit scheme), this
     * will return the scheme "file".
     * @return the scheme String for the URI backing this {@code IOResource}, if any. Will not be  null.
     */
    default String getScheme() {
        return getURI().getScheme();
    }

    /**
     * Get a {@code InputStream} for this resource.
     * @return {@code InputStream} for this resource.
     */
    InputStream getInputStream();

    /**
     * Get an {@code OutputStream} for this resource.
     * @return {@code OutputStream} for this URI.
     */
    OutputStream getOutputStream();
}
