package com.alvarium.sign;
import com.alvarium.sign.SignTypeException;
import com.alvarium.sign.SignType;

public class SignProviderFactory {

  public SignProvider getProvider(SignType type) throws SignTypeException {
    switch(type) {
      default:
        throw new SignTypeException("Concrete type not found: " + type.toString());
    }
  }

}