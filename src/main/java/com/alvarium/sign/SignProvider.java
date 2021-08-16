package com.alvarium.sign;

public interface SignProvider {
  String sign(byte[] key, byte[] content);
  boolean verify(byte[] key, byte[] content, byte[] signed);
} 
