import $ivy.`de.tototec::de.tobiasroeser.mill.vcs.version::0.4.1`

import de.tobiasroeser.mill.vcs.version.VcsVersion
import mill._
import mill.define.ModuleRef
import mill.scalalib._
import mill.scalalib.publish._

import scala.util.Properties

trait DirectoriesPublishModule extends PublishModule {
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
}

object directories extends JavaModule with DirectoriesPublishModule {
  def javacOptions = super.javacOptions() ++ Seq(
    "--release", "8"
  )
  def javadocOptions = super.javadocOptions() ++ Seq(
    "-Xdoclint:none"
  )

  def sources = T.sources {
    Seq(PathRef(T.workspace / "src/main/java"))
  }

  def jdk22ClassesResources = T {
    val destDir = T.dest / "META-INF/versions/22"
    os.makeDir.all(destDir)
    for (elem <- os.list(jdk22.compile().classes.path))
      os.copy(elem, destDir / elem.last)
    PathRef(T.dest)
  }

  def resources = T {
    T.sources(Seq(PathRef(T.workspace / "src/main/resources")) ++ Seq(jdk22ClassesResources()))
  }
  def manifest = T {
    super.manifest().add("Multi-Release" -> "true")
  }

  def localRepo = Task {
    val dest = Task.dest

    new LocalIvyPublisher(T.dest).publishLocal(
      jar = jar().path,
      sourcesJar = sourceJar().path,
      docJar = docJar().path,
      pom = pom().path,
      ivy = ivy().path,
      artifact = artifactMetadata(),
      extras = extraPublish()
    )

    PathRef(dest)
  }
}

object `directories-jni` extends JavaModule with DirectoriesPublishModule {
  def moduleDeps = Seq(directories)
  def ivyDeps = Agg(
    ivy"io.get-coursier.jniutils:windows-jni-utils:0.3.3"
  )
  def javacOptions = super.javacOptions() ++ Seq(
    "--release", "8"
  )

  def localRepo = Task {
    val dest = Task.dest

    new LocalIvyPublisher(T.dest).publishLocal(
      jar = jar().path,
      sourcesJar = sourceJar().path,
      docJar = docJar().path,
      pom = pom().path,
      ivy = ivy().path,
      artifact = artifactMetadata(),
      extras = extraPublish()
    )

    PathRef(dest)
  }
}

object jdk22 extends JavaModule {
  def moduleDeps = Seq(directories)
  def javacOptions = super.javacOptions() ++ Seq(
    "--release", "22"
  )
}

object java11ZincWorker extends ZincWorkerModule {
  override def jvmId =
    if (Properties.isMac) "zulu:8"
    else "8"
}

trait Tests extends Cross.Module[String] with JavaModule {
  def zincWorker = crossValue match {
    case "11" => ModuleRef(java11ZincWorker)
    case "default" => super.zincWorker
  }

  def ivyDeps = Agg(
    ivy"${directories.pomSettings().organization}:directories:${directories.publishVersion()}"
  )
  def repositoriesTask = Task.Anon {
    Seq(
      coursier.parse.RepositoryParser.repository(
        "ivy:" + directories.localRepo().path.toNIO.toUri.toASCIIString + "[defaultPattern]"
      ).fold(err => throw new Exception(err), x => x)
    ) ++ super.repositoriesTask()
  }

  object test extends JavaTests {
    def ivyDeps = super.ivyDeps() ++ Agg(
      ivy"junit:junit:4.13",
      ivy"com.novocode:junit-interface:0.11"
    )
    def testFramework = "com.novocode.junit.JUnitFramework"
  }
}

trait TestsJni extends Cross.Module[String] with JavaModule {
  def zincWorker = crossValue match {
    case "11" => ModuleRef(java11ZincWorker)
    case "default" => super.zincWorker
  }

  def ivyDeps = Agg(
    ivy"${`directories-jni`.pomSettings().organization}:directories-jni:${`directories-jni`.publishVersion()}"
  )
  def repositoriesTask = Task.Anon {
    Seq(
      coursier.parse.RepositoryParser.repository(
        "ivy:" + directories.localRepo().path.toNIO.toUri.toASCIIString + "[defaultPattern]"
      ).fold(err => throw new Exception(err), x => x),
      coursier.parse.RepositoryParser.repository(
        "ivy:" + `directories-jni`.localRepo().path.toNIO.toUri.toASCIIString + "[defaultPattern]"
      ).fold(err => throw new Exception(err), x => x)
    ) ++ super.repositoriesTask()
  }

  object test extends JavaTests {
    def ivyDeps = super.ivyDeps() ++ Agg(
      ivy"junit:junit:4.13",
      ivy"com.novocode:junit-interface:0.11"
    )
    def testFramework = "com.novocode.junit.JUnitFramework"
  }
}

object tests extends Cross[Tests]("11", "default")

object `tests-jni` extends Cross[TestsJni](
  if (Properties.isWin) Seq("11", "default")
  else Seq.empty[String]
)
