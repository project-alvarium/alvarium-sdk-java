package com.alvarium.sign;

public class SignProviderFactory {

  public SignProvider getProvider(SignType type) throws SignException {
    switch(type) {
      case Ed25519: 
        return new Ed25519Provider();
      default:
        throw new SignException("Concrete type not found: " + type.toString(), null);
    }
  }

}