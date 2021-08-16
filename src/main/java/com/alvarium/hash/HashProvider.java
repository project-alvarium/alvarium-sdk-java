package com.alvarium.hash;

public interface HashProvider {
  //converts byte data to an hash value
  String derive(byte[] data);
}
