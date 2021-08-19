package com.alvarium.hash;

public class HashProviderFactory {

  public HashProvider getProvider(HashType type) throws HashTypeException {
    switch (type) {
      case SHA256Hash:
        return new Sha256Provider();
      case MD5Hash:
        return new Md5Provider();
      case NoHash:
        return new NoneProvider();
      default:
        throw new HashTypeException(String.format("type %s is not supported"
            , type));
    }
  }  

}

