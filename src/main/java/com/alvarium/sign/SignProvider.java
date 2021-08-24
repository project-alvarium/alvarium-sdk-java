package com.alvarium.sign;

public interface SignProvider {
  /**
   * Uses a private key to sign content and returns the signature
   * in hex format stored in a string
   * @param key
   * @param content
   * @return signature
   * @throws SignException
   */
  String sign(byte[] key, byte[] content) throws SignException;

  /**
   * Verifies a signature against content using a public key
   * @param key
   * @param content
   * @param signed
   * @throws SignException when verification does not pass
   */
  void verify(byte[] key, byte[] content, byte[] signed) throws SignException;
} 
