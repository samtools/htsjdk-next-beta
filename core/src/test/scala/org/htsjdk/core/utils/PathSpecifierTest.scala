package org.htsjdk.core.utils

import java.io.{DataInputStream, DataOutputStream}
import java.nio.file.{FileSystems, Path, Paths}

import com.google.common.jimfs.{Configuration, Jimfs}
import org.apache.commons.lang3.SystemUtils
import org.htsjdk.core.UnitTest

class PathSpecifierTest extends UnitTest {
  private val Sep = FileSystems.getDefault.getSeparator
  private val CwdPath:     String = Paths.get(".").normalize().toAbsolutePath.toString
  private val CwdUriPath:  String = Paths.get(".").toUri.normalize().getPath
  private val RootUriPath: String = Paths.get("/").toUri.normalize().getPath
  private val StdIn:  String      = if (SystemUtils.IS_OS_WINDOWS) "-" else "/dev/stdin"
  private val StdOut: String      = if (SystemUtils.IS_OS_WINDOWS) "-" else "/dev/stdout"

  case class Test(input: String, uri: String, nio: Boolean, path: Boolean)
  object Test {
    def apply(input: Path, uri: String, nio: Boolean, path: Boolean): Test = Test(input.toString, uri, nio, path)
  }

  val ValidInputs = Seq(
    Test("localFile.bam", "file://" + CwdUriPath + "localFile.bam", nio = true, path = true),
    // absolute reference to a file in the root of the current file system (Windows accepts the "/" as root)
    Test("/localFile.bam", "file://" + RootUriPath + "localFile.bam", nio = true, path = true),
    // absolute reference to a file in the root of the current file system, where root is specified using the default FS separator
    Test(Sep + "localFile.bam",  "file://" + RootUriPath + "localFile.bam", nio = true, path = true),
    // absolute reference to a file
    Test("/path" / "to" / "localFile.bam", "file://" + RootUriPath + "path/to/localFile.bam",  nio = true, path = true),
    // absolute reference to a file that contains a URI "excluded" character in the path ("#"), which without
    // encoding will be treated as a fragment delimiter
    Test("/project" / "gvcf-pcr" / "23232_1#1" / "1.g.vcf.gz", "file://" + RootUriPath+ "project/gvcf-pcr/23232_1%231/1.g.vcf.gz", nio = true, path = true),
    // relative reference to a file on the local file system
    Test("path" / "to" / "localFile.bam", "file://" + CwdUriPath + "path/to/localFile.bam", nio = true, path = true),
    // Windows also accepts "/" as a valid root specifier
    Test("/", "file://" + RootUriPath, nio = true, path = true),
    Test(".", "file://" + CwdUriPath + "./", nio = true, path = true),
    Test("../.", "file://" + CwdUriPath + ".././", nio = true, path = true),
    // an empty path is equivalent to accessing the current directory of the default file system
    Test("", "file://" + CwdUriPath, nio = true, path = true),

    //***********************************************************
    // Local file references using a URI with a "file://" scheme.
    //***********************************************************

    Test("file:localFile.bam",              "file:localFile.bam",           nio = true, path = false), // absolute, opaque (not hierarchical)
    Test("file:/localFile.bam",             "file:/localFile.bam",          nio = true, path = true),  // absolute, hierarchical
    Test("file://localFile.bam",            "file://localFile.bam",         nio = true, path = false), // file URLs can't have an authority ("localFile.bam")
    Test("file:///localFile.bam",           "file:///localFile.bam",        nio = true, path = true),  // empty authority
    Test("file:path/to/localFile.bam",      "file:path/to/localFile.bam",   nio = true, path = false),
    Test("file:/path/to/localFile.bam",     "file:/path/to/localFile.bam",  nio = true, path = true),
    // "path" appears to be an authority, and will be accepted on Windows since this URI will be
    // interpreted as a UNC path containing an authority
    Test("file://path/to/localFile.bam",    "file://path/to/localFile.bam", nio = true, SystemUtils.IS_OS_WINDOWS),
    // "localhost" is accepted as a special case authority for "file://" Paths on Windows; but not Linux/Mac
    Test("file://localhost/to/localFile.bam","file://localhost/to/localFile.bam", nio = true, SystemUtils.IS_OS_WINDOWS),
    Test("file:///path/to/localFile.bam",   "file:///path/to/localFile.bam",    nio = true, path = true),  // empty authority

    //*****************************************************************************
    // Valid URIs which are NOT valid NIO paths (no installed file system provider)
    //*****************************************************************************

    Test("gs://file.bam",                   "gs://file.bam",                    nio = false, path = false),
    Test("gs://bucket/file.bam",            "gs://bucket/file.bam",             nio = false, path = false),
    Test("gs:///bucket/file.bam",           "gs:///bucket/file.bam",            nio = false, path = false),
    Test("gs://auth/bucket/file.bam",       "gs://auth/bucket/file.bam",        nio = false, path = false),
    Test("gs://hellbender/test/resources/", "gs://hellbender/test/resources/",  nio = false, path = false),
    Test("gcs://abucket/bucket",            "gcs://abucket/bucket",             nio = false, path = false),
    Test("gendb://somegdb",                 "gendb://somegdb",                  nio = false, path = false),
    Test("chr1:1-100",                      "chr1:1-100",                       nio = false, path = false),

    //*****************************************************************************************
    // Valid URIs which are backed by an installed NIO file system provider), but are which not
    // actually resolvable as paths because the scheme-specific part is not valid for one reason
    // or another.
    //**********************************************************************************************

    // uri must have a path: jimfs:file.bam
    Test("jimfs:file.bam",      "jimfs:file.bam", nio = true, path = false),
    // java.lang.AssertionError: java.net.URISyntaxException: Expected scheme-specific part at index 6: jimfs:
    Test("jimfs:/file.bam",     "jimfs:/file.bam", nio = true, path = false),
    // java.lang.AssertionError: uri must have a path: jimfs://file.bam
    Test("jimfs://file.bam",    "jimfs://file.bam", nio = true, path = false),
    // java.lang.AssertionError: java.net.URISyntaxException: Expected scheme-specific part at index 6: jimfs:
    Test("jimfs:///file.bam",   "jimfs:///file.bam", nio = true, path = false),
    // java.nio.file.FileSystemNotFoundException: jimfs://root
    Test("jimfs://root/file.bam","jimfs://root/file.bam", nio = true, path = false),

    //***********************************************************************************************
    // References that contain characters that require URI-encoding. If the input string is presented
    // without no scheme, it will be be automatically encoded by PathSpecifier, otherwise it
    // must already be URI-encoded.
    //***********************************************************************************************

    // relative (non-URI) reference to a file on the local file system that contains a URI fragment delimiter
    // is automatically URI-encoded
    Test("project" / "gvcf-pcr" / "23232_1#1" / "1.g.vcf.gz", "file://" + CwdUriPath + "project/gvcf-pcr/23232_1%231/1.g.vcf.gz", nio = true, path = true),
    // URI reference with fragment delimiter is not automatically URI-encoded
    Test("file:project/gvcf-pcr/23232_1#1/1.g.vcf.gz",  "file:project/gvcf-pcr/23232_1#1/1.g.vcf.gz", nio = true, path = false),
    Test("file:/project/gvcf-pcr/23232_1#1/1.g.vcf.gz", "file:/project/gvcf-pcr/23232_1#1/1.g.vcf.gz", nio = true, path = false),
    Test("file:///project/gvcf-pcr/23232_1%231/1.g.vcf.g", "file:///project/gvcf-pcr/23232_1%231/1.g.vcf.g", nio = true, path = true),
  )

  "PathSpecifier" should "be able to open an input stream from stdin" in {
    val spec  = new PathSpecifier(StdIn)
    val in    = new DataInputStream(spec.getInputStream)
    val bytes = new Array[Byte](0)
    in.readFully(bytes)
    new String(bytes) shouldBe ""
  }

  it should "be able to open up an output stream from stdout" in {
    val spec  = new PathSpecifier(StdOut)
    val out   = new DataOutputStream(spec.getOutputStream)
    out.write("some stuff".getBytes)
  }

  ValidInputs.zipWithIndex.foreach { case(in, index) =>
    it should s"correctly handle input #$index '${in.input}'" in {
      val spec = new PathSpecifier(in.input)
      spec.getURIString shouldBe in.uri
      spec.isNIO        shouldBe in.nio
      spec.isPath       shouldBe in.path
      if (in.path) spec.toPath should not be null
    }
  }

  it should "throw an exception on an invalid input string" in {
    an[IllegalArgumentException] shouldBe thrownBy { new PathSpecifier("\u0000")}
  }

  Seq(
    ("file:/project/gvcf-pcr/23232_1#1/1.g.vcf.gz",   "not URL encoded"),
    ("file:project/gvcf-pcr/23232_1#1/1.g.vcf.gz",    "scheme-specific part is not hierarchical"),
    ("hdfs://userinfo@host:80/path/to/file.bam",      "hdfs doesn't allow invalid hosts"),
    ("unknownscheme://foobar",                        "URI with an unknown scheme"),
    ("gendb://adb",                                   "???"),
    ("gcs://abucket/bucket",                          "valid URI that isn't a path"),
    ("file://nonexistent_authority/path/to/file.bam", "file URI with unknown authority nonexistent_authority")
  ).foreach { case (input, desc) =>
    it should s"not see $input as a path because: $desc" in {
      val spec = new PathSpecifier(input)
      spec.isPath shouldBe false
      an[Exception] shouldBe thrownBy { spec.toPath }
    }
  }

  it should "work with a non-default NIO filesystem" in {
    val jimfs      = Jimfs.newFileSystem(Configuration.unix)
    val outputPath = jimfs.getPath("alternateFileSystemTest.txt")
    val contents   = "Test contents"
    val spec       = new PathSpecifier(outputPath.toUri.toString)
    val out        = new DataOutputStream(spec.getOutputStream)
    out.write(contents.getBytes)
    out.close()

    // read it back in and make sure it matches expected contents
    val in    = new DataInputStream(spec.getInputStream)
    val bytes = new Array[Byte](contents.length * 2)
    val count = in.read(bytes)
    in.close()
    jimfs.close()
    count shouldBe contents.length
    new String(bytes, 0, count) shouldBe contents
  }

  Seq(
    (".." / "data" / "utils" / "testTextFile.txt",           "Test file."), // relative (file) reference to a local file
    (CwdPath / ".." / "data" / "utils" / "testTextFile.txt", "Test file."), // absolute reference to a local file
    (s"file://$CwdUriPath//../data/utils/testTextFile.txt",  "Test file."), // URI reference to a local file, where the path is absolute

    // reference to a local file with an embedded fragment delimiter ("#") in the name; if the file
    // scheme is included, the rest of the path must already be encoded; if no file scheme is
    // included, the path is encoded by the PathSpecifier class
    (".." / "data" / "utils" / "testDirWith#InName" / "testTextFile.txt",       "Test file."),
    (s"file://$CwdUriPath/../data/utils/testDirWith%23InName/testTextFile.txt", "Test file."),
  ).foreach { case (input, contents) =>
    it should s"be able to generate an input stream from $input" in {
      val spec   = new PathSpecifier(input.toString)
      val stream = new DataInputStream(spec.getInputStream)
      val bytes  = new Array[Byte](contents.length * 2)
      val count  = stream.read(bytes)
      stream.close()

      count shouldBe contents.length
      new String(bytes, 0, count) shouldBe contents
    }
  }

  it should "be able to generate an output stream for valid paths" in {
    val path = makeTempFile("some_text_file.", ".txt")
    val pathOnlyString = path.toUri().normalize().getPath()
    new PathSpecifier(path.toString()).getOutputStream.close()
    new PathSpecifier(s"file://$pathOnlyString" ).getOutputStream.close()
  }
}
