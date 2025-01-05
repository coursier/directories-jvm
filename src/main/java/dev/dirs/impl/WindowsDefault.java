package dev.dirs.impl;

import java.util.function.Supplier;

final class WindowsDefault {

  static Supplier<Windows> getDefaultSupplier() {
    return () -> new WindowsForeign();
  }

}
