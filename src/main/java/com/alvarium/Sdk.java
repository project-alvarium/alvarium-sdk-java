package com.alvarium;

import com.alvarium.annotators.AnnotatorException;
import com.alvarium.streams.StreamException;
import com.alvarium.utils.PropertyBag;

public interface Sdk {
  /**
   * Annotates incoming data based on the list of annotators that was provided to the sdk and
   * publishes it to the given StreamProvider.
   * 
   * @param properties : A property bag that may be used by specific (or custom) annotators to pass
   * custom values to them. The built-in annotators that require custom values are
   * <ul>
   * <li>TLS: Takes a key-value pair of "TLS": Socket</li>
   * </ul>
   * @param data : data being annotated
   * @throws AnnotatorException
   * @throws StreamException
   */
  public void create(PropertyBag properties, byte[] data) 
      throws AnnotatorException, StreamException;

  /**
   * Annotates incoming data based on the list of annotators that was provided to the sdk and 
   * publishes it to the given StreamProvider.
   * @param data : data being annotated
   * @throws AnnotatorException
   * @throws StreamException
   */
  public void create(byte[] data) throws AnnotatorException, StreamException;

  /**
   * Used when the recieved piece of data is originated by a separate application.
   * The data is being transitioned from one application to another.
   * @param properties : A property bag that may be used by specific (or custom) annotators to pass
   * custom values to them. The built-in annotators that require custom values are
   * <ul>
   * <li>TLS: Takes a key-value pair of "TLS": Socket</li>
   * </ul>
   * @param data : data being annotated
   * @throws AnnotatorException
   * @throws StreamException
   */
  public void transit(PropertyBag properties, byte[] data) 
      throws AnnotatorException, StreamException;

  /**
   * Used when the recieved piece of data is originated by a separate application.
   * The data is being transitioned from one application to another.
   * @param data
   * @throws AnnotatorException
   * @throws StreamException
   */
  public void transit(byte[] data) throws AnnotatorException, StreamException;
  /**
   * Handles annotations related to any type of data modification.
   * @param properties : A property bag that may be used by specific (or custom) annotators to pass
   * custom values to them. The built-in annotators that require custom values are
   * <ul>
   * <li>TLS: Takes a key-value pair of "TLS": Socket</li>
   * </ul>
   * @param oldData : original data
   * @param newData : incoming new data
   * @throws AnnotatorException
   * @throws StreamException
   */
  public void mutate(PropertyBag properties, byte[] oldData, byte[] newData) throws 
      AnnotatorException, StreamException;

  /**
   * Handles annotations related to any type of data modification.
   * @throws StreamException
   */
  public void mutate(byte[] oldData, byte[] newData) throws AnnotatorException, StreamException;

  /**
   * Closes any open connections 
   * @throws StreamException
   */
  public void close() throws StreamException;
}
