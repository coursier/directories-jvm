package dev.dirs.jni;

import coursier.jniutils.WindowsKnownFolders;
import dev.dirs.impl.Windows;

import java.util.function.Supplier;

public final class WindowsJni implements Windows {

  public String[] winDirs(String... folderIds) {
    String[] dirs = new String[folderIds.length];
    for (int i = 0; i < folderIds.length; i++) {
      dirs[i] = WindowsKnownFolders.knownFolderPath("{" + folderIds[i] + "}");
    }
    return dirs;
  }

  public static Supplier<Windows> getJdkAwareSupplier() {
    String javaVersion = System.getProperty("java.version", "0");
    if (javaVersion.substring(0, "1.".length()).equals("1."))
      javaVersion = javaVersion.substring("1.".length());
    int dotIdx = javaVersion.indexOf('.');
    if (dotIdx >= 0)
      javaVersion = javaVersion.substring(0, dotIdx);
    int jdkVersion = Integer.parseInt(javaVersion);
    if (jdkVersion >= 23)
      return Windows.getDefaultSupplier();
    else
      return () -> new WindowsJni();
  }

}
