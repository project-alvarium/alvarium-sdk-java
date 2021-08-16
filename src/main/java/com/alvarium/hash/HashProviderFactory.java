package com.alvarium.hash;

public class HashProviderFactory {

  public HashProvider getProvider(HashType type) throws HashTypeException {
    switch (type) {
      case NoHash:
        return new NoneProvider();
      default:
        throw new HashTypeException(String.format("type %s is not supported"
            , type));
    }
  }  

}

