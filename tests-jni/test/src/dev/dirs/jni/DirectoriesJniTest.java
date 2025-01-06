package dev.dirs.jni;

import dev.dirs.*;
import org.junit.Test;

public final class DirectoriesJniTest {

  @Test
  public void testBaseDirectories() {
    BaseDirectories baseDirs = BaseDirectories.get(WindowsJni.getJdkAwareSupplier());
    System.out.println(baseDirs);
  }

  @Test
  public void testUserDirectories() {
    UserDirectories userDirs = UserDirectories.get(WindowsJni.getJdkAwareSupplier());
    System.out.println(userDirs);
  }

  @Test
  public void testProjectDirectories() {
    ProjectDirectories projDirs = ProjectDirectories.from("org" /*qualifier*/, "Baz Corp" /*organization*/, "Foo Bar-App" /*project*/, WindowsJni.getJdkAwareSupplier());
    System.out.println(projDirs);
  }
}
