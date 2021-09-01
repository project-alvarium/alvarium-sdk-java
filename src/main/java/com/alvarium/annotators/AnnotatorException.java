package com.alvarium.annotators;

/**
 * a general exception type to be used by the annotators
 */
public class AnnotatorException extends Exception {
  public AnnotatorException(String msg) {
    super(msg);
  }

  public AnnotatorException(String msg, Exception e) {
    super(msg, e);
  }
}
