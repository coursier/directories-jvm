package dev.dirs.jni;

import dev.dirs.Constants;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public final class UtilJniTest {

  static {
    System.out.println("Tests Java version: " + System.getProperty("java.version"));
  }

  @Test
  public void testPowershellOne() {
    if (Constants.operatingSystem == 'w') {
      String[] winDirs = WindowsJni.getJdkAwareSupplier().get().winDirs("3EB685DB-65F9-4CF6-A03A-E3EF65729F3D");
      for (String winDir : winDirs) {
        assertNotNull(winDir);
      }
    }
  }

  @Test
  public void testPowershell() {
    if (Constants.operatingSystem == 'w') {
      String[] winDirs = WindowsJni.getJdkAwareSupplier().get().winDirs("3EB685DB-65F9-4CF6-A03A-E3EF65729F3D", "F1B32785-6FBA-4FCF-9D55-7B8E7F157091");
      for (String winDir : winDirs) {
        assertNotNull(winDir);
      }
    }
  }

}
