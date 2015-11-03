package com.genequery.commons.utils;

import java.util.*;

/**
 * Created by Arbuzov Ivan.
 */
public class StringUtils {
  public static String join(Collection collection, String delimiter) {
    StringBuilder sb = new StringBuilder();

    for (Iterator iterator = collection.iterator(); iterator.hasNext(); sb.append(String.valueOf(iterator.next()))) {
      if (sb.length() != 0) {
        sb.append(delimiter);
      }
    }

    return sb.toString();
  }

  public static String match(String template, Object... args) {
    if (template == null) throw new IllegalArgumentException("string template is null");
    if (args == null) throw new IllegalArgumentException("args is null");

    String[] partsArray = template.split("\\{\\}");
    List<String> parts = new ArrayList<>();

    parts.addAll(Arrays.asList(partsArray));
    if (template.endsWith("{}")) parts.add("");
    if (template.equals("{}")) parts.add("");

    if (parts.size() - 1 != args.length) {
      throw new IllegalArgumentException(
          "Bad arguments: " + args.length + " is given, " + (parts.size() - 1) + " is expected.");
    }
    StringBuilder sb = new StringBuilder();
    sb.append(parts.get(0));
    for (int i = 1; i < parts.size(); ++i) {
      sb.append(args[i - 1].toString());
      sb.append(parts.get(i));
    }
    return sb.toString();
  }

  public static void main(String[] args) {
    System.out.println(match("{}Hel{}lo{}", 1, 2, 4));
  }

}
