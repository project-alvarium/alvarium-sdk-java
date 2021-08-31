package com.alvarium.streams;

import com.alvarium.PublishWrapper;

/**
 * A unit that provides a way for the sdk to connect and publish data to an external pub/sub model
 */
public interface StreamProvider {
  /**
   * connects to an external unit to be able to publish ongoing data
   * @throws StreamException: thrown when it fails to connect to the external unit
   */
  public void connect() throws StreamException;

  /**
   * closes the previously opened connection
   * @throws StreamException: thrown when the connection is already closed
   */
  public void close() throws StreamException;

  /**
   * publishes the passed data to the external unit through the established connection
   * @param wrapper : data being published
   * @throws StreamException: thrown if the connection is closed or if the unit did not respond
   */
  public void publish(PublishWrapper wrapper) throws StreamException; 
}
