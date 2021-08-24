package com.alvarium.sign;

public class SignException extends Exception{
  public SignException(String message, Exception e) {
    super(message, e, false, true);
  }
}
