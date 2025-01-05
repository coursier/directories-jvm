[![Maven Central](https://img.shields.io/maven-central/v/io.get-coursier.util/directories.svg?style=for-the-badge)](https://search.maven.org/#search|gav|1|g%3A%22io.get-coursier.util%22%20AND%20a%3A%22directories%22)
[![API documentation](http://javadoc.io/badge/io.get-coursier.util/directories.svg?style=for-the-badge)](http://javadoc.io/doc/io.get-coursier.util/directories)
![actively developed](https://img.shields.io/badge/maintenance-actively_developed-brightgreen.svg?style=for-the-badge)
[![GitHub Actions status](https://img.shields.io/github/actions/workflow/status/coursier/directories-jvm/ci.yml?branch=main&style=for-the-badge)](https://github.com/coursier/directories-jvm/actions/workflows/ci.yml)
[![License: MPL-2.0](https://img.shields.io/github/license/coursier/directories-jvm.svg?style=for-the-badge)](LICENSE)

# Coursier directories-jvm

Fork of of the [dirs-dev/directories-jvm](https://github.com/dirs-dev/directories-jvm) project, with [coursier](https://github.com/coursier/coursier)-specific changes

Compared to the upstream project, this fork:
* still supports JDK 8
* offers to use JNI rather than shelling out to a PowerShell script to call Windows APIs with JDK < 23

## How to use

### Add dependency

#### Mill
```scala
def ivyDeps = Agg(
  ivy"io.get-coursier.util:directories:0.1.2"
)
```

#### Scala CLI
```scala
//> using dep io.get-coursier.util:directories:0.1.2
```

### API

```scala
import dev.dirs.ProjectDirectories
val projDirs = ProjectDirectories.from(null, null, "MyApp")
projDirs.configDir // "/Users/name/Library/Application Support/MyApp"
projDirs.preferenceDir // "/Users/name/Library/Preferences/MyApp"
// ...
```

## How to use with JNI fallback on JDK < 23

### Add dependency

#### Mill
```scala
def ivyDeps = Agg(
  ivy"io.get-coursier.util:directories-jni:0.1.2"
)
```

#### Scala CLI
```scala
//> using dep io.get-coursier.util:directories-jni:0.1.2
```

### API

```scala
import dev.dirs.ProjectDirectories
import dev.dirs.jni.WindowsJni
val projDirs = ProjectDirectories.from(null, null, "MyApp", WindowsJni.getJdkAwareSupplier())
projDirs.configDir // "/Users/name/Library/Application Support/MyApp"
projDirs.preferenceDir // "/Users/name/Library/Preferences/MyApp"
// ...
```
