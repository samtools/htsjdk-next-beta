package org.samtools.htsjdk.core.io;

import java.nio.ByteBuffer;

/**
 * Interface for classes that produce binary data (i.e. bytes) that can be decoded
 * into other datatypes.
 *
 * Methods should throw exceptions if:
 * - The underlying data source/stream/channel is closed
 * - There are insufficient bytes available (after blocking) to read a single item of the datatype requested
 *
 * NOTES:
 *   - Almost all the methods here could have default implementations that read via one of the readBytes() methods.
 *   - Should think through how to deal with unsigned types
 *   - Need to think how this would work with BlockCompressed data
 */
public interface ByteSource extends AutoCloseable {
    /** Returns a description of the underlying source of the bytes, e.g. a path, stdin, a URL etc. */
    String getDescription();

    // Methods related to buffering/repositioning/skipping etc.
    /** Returns true if the source has a known finite length, false otherwise. */
    boolean hasLength();

    /** Returns the length if {@link #hasLength()} is true, otherwise throws an exception. */
    default long length() { throw new UnsupportedOperationException(); }

    /** Returns the offset of the next byte that will be read by any operation. */
    long position();

    /** Returns true if the source can skip bytes without reading them. */
    boolean canNavigateForward();

    /** Returns true if the source can always be repositioned behind the current position. Note that a buffered
     * source may allow limited backwards navigation within the buffered data, while returning false here. */
    boolean canNavigateBackwards();

    /** Position the source at numBytes after the current position. May be implemented by skipping if the
     * underlying source is capable of skipping, or by reading and discarding numBytes.
     *
     * @param numBytes the number of bytes desired to be skipped.
     * @return either numBytes or a smaller number if EOF is hit before numBytes is read
     */
    int navigateForward(final long numBytes); // TODO: implement default method that reads

    /** Position the source at numBytes before the current position.
     *
     * @param numBytes the number of bytes desired to be skipped.
     * @return either numBytes or a smaller number if numBytes > position
     */
    default int navigateBackward(final long numBytes) {
        throw new UnsupportedOperationException();
    }

    /** Positions the source such that the next byte read will be at offset `position`. */
    default int navigateToPosition(final long position) {
        throw new UnsupportedOperationException();
    }

    // Methods for actually reading stuff

    /** Attempt to fill the array with bytes and returns the number of bytes read. */
    int readBytes(final byte[] buffer);

    /** Attempt to fill the regions of the array with bytes and returns the number of bytes read. */
    int readBytes(final byte[] buffer, final int offset, final int length);

    /** Attempt to read numBytes bytes into the buffer starting at the buffer's current position. */
    int readBytes(final ByteBuffer buffer, final int numBytes);

    /** Reads a single byte. */
    byte readByte();

    /** Reads a boolean. */
    boolean readBoolean();

    /** Reads a little endian signed short. */
    short readShort();

    /** Reads a little endian signed int. */
    int readInt();

    /** Reads a little endian signed long. */
    long readLong();

    /** Reads a little endian unsigned byte. */
    short readUnsignedByte();

    /** Reads a little endian unsigned short. */
    int readUnsignedShort();

    /** Reads a little endian unsigned int. */
    long readUnsignedInt();

    /** Reads a little endian float. */
    float readFloat();

    /** Reads a little endian double. */
    double readDouble();

    /** Reads an ascii string, with one-byte per character, of the given length. */
    String readAsciiString(final int length);

    /** Reads a null-terminated string, with one byte per character, of unknown length. */
    String readNullTerminatedAsciiString();
}
