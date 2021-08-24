package com.alvarium.utils;

public class Encoder {
  /**
   * Converts an array of bytes to the corresponding
   * string hexadecimal representation
   * @param data byte array of data
   * @return string hexadecimal representation
   */
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
  
  /**
   * Converts a hex string to a byte array
   * and will return the conversion of complete bytes (i.e.
   * last value in odd-sized input will be ignored
   * @param hex Hexadecimal value in string format
   * @return byte array from the hex input
   */
  public static byte[] hexToBytes(String hex) {
    // Remove incomplete bytes if odd length
    int len = hex.length();
    if (len % 2 != 0) {
      hex = hex.substring(0, len-1); // Remove last element
    }

    len = hex.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
        data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
            + Character.digit(hex.charAt(i+1), 16));
    }
    return data;
}
}
