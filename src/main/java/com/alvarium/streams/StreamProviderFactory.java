package com.alvarium.streams;


/**
 * A factory that provides different implementations of the StreamProvider interface
 */
public class StreamProviderFactory {
  public StreamProvider getProvider(StreamInfo info) throws StreamException {
    switch (info.getType()) {
      case MQTT:
        try {
          MqttConfig config = MqttConfig.class.cast(info.getConfig());
          return new MqttStreamProvider(config);
        } catch(ClassCastException e) {
          throw new StreamException("Invalid stream config", e);
        }

      case MOCK:
        return new MockStreamProvider();
    
      default:
        throw new StreamException(String.format("%s is not supported", info.getType()));
    }  
  }
}
