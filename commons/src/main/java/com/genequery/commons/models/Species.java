package com.genequery.commons.models;

/**
 * Created by Arbuzov Ivan.
 */
public enum Species {
  MOUSE("mm"), HUMAN("hs"), RAT("rt");

  private final String text;

  Species(String text) {
    this.text = text;
  }

  public String getText() {
    return this.text;
  }

  public static Species fromString(String text) {
    if (text != null) {
      for (Species b : Species.values()) {
        if (text.equalsIgnoreCase(b.text)) {
          return b;
        }
      }
    }
    throw new IllegalArgumentException("No species enum for: " + text);
  }
}
