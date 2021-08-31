package com.alvarium.streams;

/**
 * A factory that provides different implementations of the StreamProvider interface
 */
public class StreamProviderFactory {
  public StreamProvider getProvider(StreamType type) throws StreamException {
    switch (type) {
      case MOCK:
        return new MockStreamProvider();
    
      default:
        throw new StreamException(String.format("%s is not supported", type));
    }  
  }
}
