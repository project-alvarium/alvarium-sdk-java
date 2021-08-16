package com.alvarium.utils;

public class Encoder {
  // Converts an array of bytes to the corresponding string hexadecimal representation
  public static String bytesToHex(byte[] data) {
    StringBuilder hexString = new StringBuilder(2 * data.length);
    for (int i = 0; i < data.length; i++) {
      String hex = Integer.toHexString(0xff & data[i]).toUpperCase();
      if (hex.length() == 1) {
        hexString.append('0');
      }
      hexString.append(hex);
    }

    return hexString.toString();
  }
}
