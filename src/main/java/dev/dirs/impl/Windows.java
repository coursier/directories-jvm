package dev.dirs.impl;

public interface Windows {

  static String applicationPath(String qualifier, String organization, String application) {
    StringBuilder buf = new StringBuilder(Math.max(Util.stringLength(organization) + Util.stringLength(application), 0));
    boolean orgPresent = !Util.isNullOrEmpty(organization);
    boolean appPresent = !Util.isNullOrEmpty(application);
    if (orgPresent) {
      buf.append(organization);
      if (appPresent)
        buf.append('\\');
    }
    if (appPresent)
      buf.append(application);
    return buf.toString();
  }

  String[] winDirs(String... folderIds);

  public static String[] getWinDirs(String... folderIds) {
    return WindowsDefault.getDefaultSupplier().get().winDirs(folderIds);
  }
}
