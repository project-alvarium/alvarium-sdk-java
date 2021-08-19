package com.alvarium.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.alvarium.utils.Encoder;

class Sha256Provider implements HashProvider {

  private final MessageDigest sha256;

  protected Sha256Provider() throws HashTypeException {
    try {
      sha256 = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      throw new HashTypeException("SHA-256 provider is not supported");
    }
  }

  // returns the hexadecimal sha256 hash representation of the given data
  public String derive(byte[] data) {
    final byte[] hashedBytes = this.sha256.digest(data);
    final String hashedString = Encoder.bytesToHex(hashedBytes);
    return hashedString; 
  }
}
