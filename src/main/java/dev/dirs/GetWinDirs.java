package dev.dirs;

import dev.dirs.impl.Windows;

public interface GetWinDirs {
    String[] getWinDirs(String... guids);

    GetWinDirs powerShellBased = Windows::getWinDirs;
}
