package com.alvarium.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.alvarium.utils.Encoder;

class Md5Provider implements HashProvider{
  private final MessageDigest md5;

  protected Md5Provider() throws HashTypeException{
    try {
      md5 = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      throw new HashTypeException("MD5 provider is not supported");
    }
  }

  public String derive(byte[] data) {
    final byte[] hashedBytes = md5.digest(data);
    final String hashedString = Encoder.bytesToHex(hashedBytes);
    return hashedString;
  }
}
