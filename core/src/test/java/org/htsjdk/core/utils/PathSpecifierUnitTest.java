package org.htsjdk.core.utils;

//TODO: NAMING: is there any useful distinction between Unit/Integration tests in this repo ?

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.apache.commons.lang3.SystemUtils;
import org.htsjdk.core.api.io.IOResource;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.*;
import java.nio.file.*;

public class PathSpecifierUnitTest {

    final static String FS_SEPARATOR = FileSystems.getDefault().getSeparator();

    @DataProvider
    public Object[][] validPathSpecifiers() {
        return new Object[][] {
                // Paths specifiers that are syntactically valid as either a relative or absolute local file
                // name, or as a URI, but which may fail isNIO or isPath

                // input String, expected resulting URI String, expected isNIO, expected isPath

                //********************************
                // Local (non-URI) file references
                //********************************

                {"localFile.bam",                   "file://" + getCWDAsURIPathString() + "localFile.bam", true, true},
                // absolute reference to a file in the root of the current file system (Windows accepts the "/" as root)
                {"/localFile.bam",                  "file://" + getRootDirectoryAsURIPathString() + "localFile.bam", true, true},
                // absolute reference to a file in the root of the current file system, where root is specified using the
                // default FS separator
                {FS_SEPARATOR + "localFile.bam",  "file://" + getRootDirectoryAsURIPathString() + "localFile.bam", true, true},
                // absolute reference to a file
                {FS_SEPARATOR + joinWithFSSeparator("path", "to", "localFile.bam"),
                        "file://" + getRootDirectoryAsURIPathString() + "path/to/localFile.bam",  true, true},
                // absolute reference to a file that contains a URI "excluded" character in the path ("#"), which without
                // encoding will be treated as a fragment delimiter
                {FS_SEPARATOR + joinWithFSSeparator("project", "gvcf-pcr", "23232_1#1", "1.g.vcf.gz"),
                        "file://" + getRootDirectoryAsURIPathString() + "project/gvcf-pcr/23232_1%231/1.g.vcf.gz", true, true},
                // relative reference to a file on the local file system
                {joinWithFSSeparator("path", "to", "localFile.bam"),
                        "file://" + getCWDAsURIPathString() + "path/to/localFile.bam", true, true},
                // Windows also accepts "/" as a valid root specifier
                {"/", "file://" + getRootDirectoryAsURIPathString(), true, true},
                {".", "file://" + getCWDAsURIPathString() + "./", true, true},
                {"../.", "file://" + getCWDAsURIPathString() + ".././", true, true},
                // an empty path is equivalent to accessing the current directory of the default file system
                {"", "file://" + getCWDAsURIPathString(), true, true},

                //***********************************************************
                // Local file references using a URI with a "file://" scheme.
                //***********************************************************

                {"file:localFile.bam",              "file:localFile.bam",           true, false}, // absolute, opaque (not hierarchical)
                {"file:/localFile.bam",             "file:/localFile.bam",          true, true},  // absolute, hierarchical
                {"file://localFile.bam",            "file://localFile.bam",         true, false}, // file URLs can't have an authority ("localFile.bam")
                {"file:///localFile.bam",           "file:///localFile.bam",        true, true},  // empty authority
                {"file:path/to/localFile.bam",      "file:path/to/localFile.bam",   true, false},
                {"file:/path/to/localFile.bam",     "file:/path/to/localFile.bam",  true, true},
                // "path" appears to be an authority, and will be accepted on Windows since this URI will be
                // interpreted as a UNC path containing an authority
                {"file://path/to/localFile.bam",    "file://path/to/localFile.bam", true, SystemUtils.IS_OS_WINDOWS},
                // "localhost" is accepted as a special case authority for "file://" Paths on Windows; but not Linux/Mac
                {"file://localhost/to/localFile.bam","file://localhost/to/localFile.bam", true, SystemUtils.IS_OS_WINDOWS},
                {"file:///path/to/localFile.bam",   "file:///path/to/localFile.bam",    true, true},  // empty authority

                //*****************************************************************************
                // Valid URIs which are NOT valid NIO paths (no installed file system provider)
                //*****************************************************************************

                {"gs://file.bam",                   "gs://file.bam",                    false, false},
                {"gs://bucket/file.bam",            "gs://bucket/file.bam",             false, false},
                {"gs:///bucket/file.bam",           "gs:///bucket/file.bam",            false, false},
                {"gs://auth/bucket/file.bam",       "gs://auth/bucket/file.bam",        false, false},
                {"gs://hellbender/test/resources/", "gs://hellbender/test/resources/",  false, false},
                {"gcs://abucket/bucket",            "gcs://abucket/bucket",             false, false},
                {"gendb://somegdb",                 "gendb://somegdb",                  false, false},
                {"chr1:1-100",                      "chr1:1-100",                       false, false},

                //*****************************************************************************************
                // Valid URIs which are backed by an installed NIO file system provider), but are which not
                // actually resolvable as paths because the scheme-specific part is not valid for one reason
                // or another.
                //**********************************************************************************************

                // uri must have a path: jimfs:file.bam
                {"jimfs:file.bam",      "jimfs:file.bam", true, false},
                // java.lang.AssertionError: java.net.URISyntaxException: Expected scheme-specific part at index 6: jimfs:
                {"jimfs:/file.bam",     "jimfs:/file.bam", true, false},
                // java.lang.AssertionError: uri must have a path: jimfs://file.bam
                {"jimfs://file.bam",    "jimfs://file.bam", true, false},
                // java.lang.AssertionError: java.net.URISyntaxException: Expected scheme-specific part at index 6: jimfs:
                {"jimfs:///file.bam",   "jimfs:///file.bam", true, false},
                // java.nio.file.FileSystemNotFoundException: jimfs://root
                {"jimfs://root/file.bam","jimfs://root/file.bam", true, false},

                //***********************************************************************************************
                // References that contain characters that require URI-encoding. If the input string is presented
                // without no scheme, it will be be automatically encoded by PathSpecifier, otherwise it
                // must already be URI-encoded.
                //***********************************************************************************************

                // relative (non-URI) reference to a file on the local file system that contains a URI fragment delimiter
                // is automatically URI-encoded
                {joinWithFSSeparator("project", "gvcf-pcr", "23232_1#1", "1.g.vcf.gz"),
                        "file://" + getCWDAsURIPathString() + "project/gvcf-pcr/23232_1%231/1.g.vcf.gz", true, true},
                // URI reference with fragment delimiter is not automatically URI-encoded
                {"file:project/gvcf-pcr/23232_1#1/1.g.vcf.gz",  "file:project/gvcf-pcr/23232_1#1/1.g.vcf.gz", true, false},
                {"file:/project/gvcf-pcr/23232_1#1/1.g.vcf.gz", "file:/project/gvcf-pcr/23232_1#1/1.g.vcf.gz", true, false},
                {"file:///project/gvcf-pcr/23232_1%231/1.g.vcf.g", "file:///project/gvcf-pcr/23232_1%231/1.g.vcf.g", true, true},
        };
    }

    @Test(dataProvider = "validPathSpecifiers")
    public void testPathSpecifier(final String referenceString, final String expectedURIString, final boolean isNIO, final boolean isPath) {
        final IOResource ioResource = new PathSpecifier(referenceString);
        Assert.assertNotNull(ioResource);
        Assert.assertEquals(ioResource.getURI().toString(), expectedURIString);
    }

    @Test(dataProvider = "validPathSpecifiers")
    public void testIsNIO(final String referenceString, final String expectedURIString, final boolean isNIO, final boolean isPath) {
        final IOResource pathURI = new PathSpecifier(referenceString);
        Assert.assertEquals(pathURI.isNIO(), isNIO);
    }

    @Test(dataProvider = "validPathSpecifiers")
    public void testIsPath(final String referenceString, final String expectedURIString, final boolean isNIO, final boolean isPath) {
        final IOResource pathURI = new PathSpecifier(referenceString);
        if (isPath) {
            Assert.assertEquals(pathURI.isPath(), isPath, pathURI.getToPathFailureReason().orElse("no failure"));
        } else {
            Assert.assertEquals(pathURI.isPath(), isPath);
        }
    }

    @Test(dataProvider = "validPathSpecifiers")
    public void testToPath(final String referenceString, final String expectedURIString, final boolean isNIO, final boolean isPath) {
        final IOResource pathURI = new PathSpecifier(referenceString);
        if (isPath) {
            final Path path = pathURI.toPath();
            Assert.assertEquals(path != null, isPath, pathURI.getToPathFailureReason().orElse("no failure"));
        } else {
            Assert.assertEquals(pathURI.isPath(), isPath);
        }
    }

    @DataProvider
    public Object[][] invalidPathSpecifiers() {
        return new Object[][] {
                // the nul character is rejected on all of the supported platforms in both local
                // filenames and URIs, so use it to test PathSpecifier constructor failure on all platforms
                {"\0"},
        };
    }

    @Test(dataProvider = "invalidPathSpecifiers", expectedExceptions = {IllegalArgumentException.class})
    public void testPathSpecifierInvalid(final String referenceString) {
        new PathSpecifier(referenceString);
    }

    @DataProvider
    public Object[][] invalidPath() {
        return new Object[][] {
                // valid references that are not valid as a path

                {"file:/project/gvcf-pcr/23232_1#1/1.g.vcf.gz"},    // not encoded
                {"file:project/gvcf-pcr/23232_1#1/1.g.vcf.gz"},     // scheme-specific part is not hierarchical

                // The hadoop file system provider explicitly throws an NPE if no host is specified and HDFS is not
                // the default file system
                //{"hdfs://nonexistent_authority/path/to/file.bam"},  // unknown authority "nonexistent_authority"
                {"hdfs://userinfo@host:80/path/to/file.bam"},           // UnknownHostException "host"

                {"unknownscheme://foobar"},
                {"gendb://adb"},
                {"gcs://abucket/bucket"},

                // URIs with schemes that are backed by an valid NIO provider, but for which the
                // scheme-specific part is not valid.
                {"file://nonexistent_authority/path/to/file.bam"},  // unknown authority "nonexistent_authority"
        };
    }

    @Test(dataProvider = "invalidPath")
    public void testIsPathInvalid(final String invalidPathString) {
        final IOResource htsURI = new PathSpecifier(invalidPathString);
        Assert.assertFalse(htsURI.isPath());
    }

    @Test(dataProvider = "invalidPath", expectedExceptions = {IllegalArgumentException.class, FileSystemNotFoundException.class})
    public void testToPathInvalid(final String invalidPathString) {
        final IOResource htsURI = new PathSpecifier(invalidPathString);
        htsURI.toPath();
    }

    @Test
    public void testInstalledNonDefaultFileSystem() throws IOException {
        // create a jimfs file system and round trip through PathSpecifier/stream
        try (FileSystem jimfs = Jimfs.newFileSystem(Configuration.unix())) {
            final Path outputPath = jimfs.getPath("alternateFileSystemTest.txt");
            doStreamRoundTrip(outputPath.toUri().toString());
        }
    }

    @DataProvider
     public Object[][] inputStreamSpecifiers() throws IOException {
        return new Object[][]{
                // references that can be resolved to an actual test file that can be read

                // relative (file) reference to a local file
                {joinWithFSSeparator("..", "data", "utils", "testTextFile.txt"), "Test file."},

                // absolute reference to a local file
                {getCWDAsFileReference() + FS_SEPARATOR + joinWithFSSeparator("..", "data", "utils", "testTextFile.txt"), "Test file."},

                // URI reference to a local file, where the path is absolute
                {"file://" + getCWDAsURIPathString() + "../data/utils/testTextFile.txt", "Test file."},

                // reference to a local file with an embedded fragment delimiter ("#") in the name; if the file
                // scheme is included, the rest of the path must already be encoded; if no file scheme is
                // included, the path is encoded by the PathSpecifier class
                {joinWithFSSeparator("..", "data", "utils", "testDirWith#InName", "testTextFile.txt"), "Test file."},
                {"file://" + getCWDAsURIPathString() + "../data/utils/testDirWith%23InName/testTextFile.txt", "Test file."},
        };
    }

    @Test(dataProvider = "inputStreamSpecifiers")
    public void testGetInputStream(final String referenceString, final String expectedFileContents) throws IOException {
        final IOResource htsURI = new PathSpecifier(referenceString);

        try (final InputStream is = htsURI.getInputStream();
             final DataInputStream dis = new DataInputStream(is)) {
            final byte[] actualFileContents = new byte[expectedFileContents.length()];
            dis.readFully(actualFileContents);

            Assert.assertEquals(new String(actualFileContents), expectedFileContents);
        }
    }

    @DataProvider
    public Object[][] outputStreamSpecifiers() throws IOException {
        return new Object[][]{
                // output URIs that can be resolved to an actual test file
                {IOUtils.createTempPath("testOutputStream", ".txt").toString()},
                {"file://" + getLocalFileAsURIPathString(IOUtils.createTempPath("testOutputStream", ".txt"))},
        };
    }

    @Test(dataProvider = "outputStreamSpecifiers")
    public void testGetOutputStream(final String referenceString) throws IOException {
        doStreamRoundTrip(referenceString);
    }

    @Test
    public void testStdIn() throws IOException {
        final IOResource htsURI = new PathSpecifier(
                SystemUtils.IS_OS_WINDOWS ?
                        "-" :
                        "/dev/stdin");
        try (final InputStream is = htsURI.getInputStream();
             final DataInputStream dis = new DataInputStream(is)) {
            final byte[] actualFileContents = new byte[0];
            dis.readFully(actualFileContents);

            Assert.assertEquals(new String(actualFileContents), "");
        }
    }

    @Test
    public void testStdOut() throws IOException {
        final IOResource pathURI = new PathSpecifier(
                SystemUtils.IS_OS_WINDOWS ?
                        "-" :
                        "/dev/stdout");
        try (final OutputStream os = pathURI.getOutputStream();
             final DataOutputStream dos = new DataOutputStream(os)) {
            dos.write("some stuff".getBytes());
        }
    }

    /**
     * Return the string resulting from joining the individual components using the local default
     * file system separator.
     *
     * This is used to create test inputs that are local file references, as would be presented by a
     * user on the platform on which these tests are running.
     */
    private String joinWithFSSeparator(String... parts) {
        return String.join(FileSystems.getDefault().getSeparator(), parts);
    }

    private void doStreamRoundTrip(final String referenceString) throws IOException {
        final String expectedFileContents = "Test contents";

        final IOResource pathURI = new PathSpecifier(referenceString);
        try (final OutputStream os = pathURI.getOutputStream();
             final DataOutputStream dos = new DataOutputStream(os)) {
            dos.write(expectedFileContents.getBytes());
        }

        // read it back in and make sure it matches expected contents
        try (final  InputStream is = pathURI.getInputStream();
             final DataInputStream dis = new DataInputStream(is)) {
            final byte[] actualFileContents = new byte[expectedFileContents.length()];
            dis.readFully(actualFileContents);

            Assert.assertEquals(new String(actualFileContents), expectedFileContents);
        }
    }

    /**
     * Get an absolute reference to the current working directory using local file system syntax and
     * the local file system separator. Used to construct valid local, absolute file references as test inputs.
     *
     * Returns a string of the form '/some/path/.` or `d:\some\path\.` on Windows
     */
    private static String getCWDAsFileReference() {
        return new File(".").getAbsolutePath();
    }

    /**
     * Get the current working directory as a locally valid, hierarchical URI string. Used to
     * construct expected URI string values for test inputs that are local file references.
     *
     * Returns '/some/path/` or `/d:/some/path/` on Windows
     */
    private String getCWDAsURIPathString() {
        return getLocalFileAsURIPathString(Paths.get("."));
    }

    /**
     * Get just the path part of the URI representing the current working directory. Used
     * to construct expected URI string values for test inputs that specify a file in the
     * root of the local file system.
     *
     * Returns a string of the form "/" or "/d:/" on Windows.
     */
    private String getRootDirectoryAsURIPathString() {
        return getLocalFileAsURIPathString(Paths.get(FS_SEPARATOR));
    }

    /**
     * Get just the path part of the URI representing a file on the local file system as a normalized
     * String.
     *
     * Returns a string of the form `/some/path' or '/d:/some/path/` on Windows.
     */
    private String getLocalFileAsURIPathString(final Path localPath) {
        return localPath.toUri().normalize().getPath();
    }

}
