package com.alvarium.sign;

public interface SignProvider {
  /**
   * Uses a private key to sign content and returns the signature in hex format stored in a string
   * 
   * The key parameter needs to be 64-byte in size where the private key is suffixed by 
   * the public key (Some implementations will accept and use only the first 32-bytes of the
   * private key such as Ed25519)
   *  
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
