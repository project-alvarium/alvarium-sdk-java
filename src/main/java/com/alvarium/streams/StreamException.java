package com.alvarium.streams;

/**
 * A general type exception that encapsulates all exceptions related to stream providers
 */
public class StreamException extends Exception {

  public StreamException(String msg) {
    super(msg);
  }

  public StreamException(String msg, Exception e) {
    super(msg, e);
  }
}
