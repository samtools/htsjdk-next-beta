package org.htsjdk.core

import java.nio.file.{Files, Path, Paths}

import org.scalatest.{FlatSpec, Matchers}

/** Base class for all Scala tests. */
class UnitTest extends FlatSpec with Matchers {
  /** Make a temporary file that will get cleaned up at the end of testing. */
  protected def makeTempFile(prefix: String, suffix: String): Path = {
    val path = Files.createTempFile(prefix, suffix)
    path.toFile.deleteOnExit()
    path
  }

  /** Implicit conversion from Java to Scala iterator. */
  implicit def javaIteratorAsScalaIterator[A](iter: java.util.Iterator[A]): Iterator[A] = {
    scala.collection.JavaConverters.asScalaIterator(iter)
  }

  /** Implicit conversion from Java to Scala iterable. */
  implicit def javaIterableAsScalaIterable[A](iterable: java.lang.Iterable[A]): Iterable[A] = {
    scala.collection.JavaConverters.iterableAsScalaIterable(iterable)
  }

  /** Small implicit class to give operator like syntax to paths. */
  implicit class PathLike(path: Path) {
    def /(sub: String): Path = path.resolve(sub)
  }

  /** Generates a PathLike from a String instead of a Path. */
  implicit def stringToPathLike(path: String): PathLike = PathLike(Paths.get(path))
}
