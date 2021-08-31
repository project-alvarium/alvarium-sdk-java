package com.alvarium.streams;

import com.alvarium.PublishWrapper;

/**
 * A dummy stream provider that does not throw any errors
 * Mainly used for unit tests
 */
class MockStreamProvider implements StreamProvider {
  public void connect() {
    System.out.println("stream connected");
  }
  
  public void close() {
    System.out.println("stream closed");
  }
  
  public void publish(PublishWrapper wrapper) {
    System.out.println(String.format("%s publish", wrapper.toJson()));
  }
}
