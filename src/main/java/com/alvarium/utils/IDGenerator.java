package com.alvarium.utils;

import de.huxhorn.sulky.ulid.ULID;

public class IDGenerator {

  private IDGenerator() {}

  private static final ULID ULID = new ULID();

  public static String generate() {
    return ULID.nextULID();
  }

}
