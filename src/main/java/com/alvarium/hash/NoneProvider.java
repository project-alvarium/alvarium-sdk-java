package com.alvarium.hash;

class NoneProvider implements HashProvider {
  @Override
  public String derive(byte[] data) {
    return new String(data);
  }
}
