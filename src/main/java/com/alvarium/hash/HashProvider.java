package com.alvarium.hash;

/**
 * A unit that provides arbitrary ways to derive hash values
 * from a given piece of data
 */
public interface HashProvider {
  /**
   * converts a byte array of data to it's hash value 
   * @param data byte array of data
   * @return hashed value of the given data
   */
  String derive(byte[] data);
}
