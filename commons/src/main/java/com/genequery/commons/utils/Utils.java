package com.genequery.commons.utils;

import org.jetbrains.annotations.Nullable;

/**
 * Created by Arbuzov Ivan.
 */
public class Utils {
  public static <T> T checkNotNull(@Nullable T obj) {
    if (obj == null)
      throw new NullPointerException();
    return obj;
  }

  public static <T> T checkNotNull(@Nullable T obj, String message) {
    if (obj == null)
      throw new NullPointerException(message);
    return obj;
  }
}
