import $ivy.`de.tototec::de.tobiasroeser.mill.vcs.version::0.4.1`

import de.tobiasroeser.mill.vcs.version.VcsVersion
import mill._
import mill.scalalib._
import mill.scalalib.publish._

object directories extends JavaModule with PublishModule {
  def pomSettings = PomSettings(
    description = "directories-jvm",
    organization = "io.get-coursier.util",
    url = "https://github.com/coursier/directories-jvm",
    licenses = Seq(License.`MPL-2.0`),
    versionControl = VersionControl.github("coursier", "directories-jvm"),
    developers = Seq(
      Developer("alexarchambault", "Alex Archambault", "https://github.com/alexarchambault")
    )
  )
  def publishVersion = T {
    val value = VcsVersion.vcsState().format()
    if (value.contains("-")) {
      val value0 = value.takeWhile(_ != '-')
      val lastDotIdx = value0.lastIndexOf('.')
      if (lastDotIdx < 0) value0 + "-SNAPSHOT"
      else
        value0.drop(lastDotIdx + 1).toIntOption match {
          case Some(lastNum) =>
            val prefix = value0.take(lastDotIdx)
            s"$prefix.${lastNum + 1}-SNAPSHOT"
          case None =>
            value0 + "-SNAPSHOT"
        }
    }
    else value
  }

  def javacOptions = super.javacOptions() ++ Seq(
    "--release", "8"
  )
  def javadocOptions = super.javadocOptions() ++ Seq(
    "-Xdoclint:none"
  )

  def sources = T.sources {
    Seq(PathRef(T.workspace / "src/main"))
  }

  def jdk23ClassesResources = T {
    val destDir = T.dest / "META-INF/versions/23"
    os.makeDir.all(destDir)
    for (elem <- os.list(jdk23.compile().classes.path))
      os.copy(elem, destDir / elem.last)
    PathRef(T.dest)
  }

  def resources = T {
    T.sources(super.resources() ++ Seq(jdk23ClassesResources()))
  }
  def manifest = T {
    super.manifest().add("Multi-Release" -> "true")
  }

  object test extends JavaTests {
    def sources = T.sources {
      Seq(PathRef(T.workspace / "src/test"))
    }
    def ivyDeps = super.ivyDeps() ++ Agg(
      ivy"junit:junit:4.13",
      ivy"com.novocode:junit-interface:0.11"
    )
    def testFramework = "com.novocode.junit.JUnitFramework"
  }
}

object jdk23 extends JavaModule {
  def moduleDeps = Seq(directories)
  def javacOptions = super.javacOptions() ++ Seq(
    "--release", "23"
  )
}
