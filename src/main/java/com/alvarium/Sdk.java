package com.alvarium;

import com.alvarium.annotators.AnnotatorException;
import com.alvarium.streams.StreamException;
import com.alvarium.utils.PropertyBag;

public interface Sdk {
  /**
   * Annotates incoming data based on the list of annotators that was provided to the sdk and
   * publishes it to the given StreamProvider.
   * @param properties : A property bag that may be used by specific annotators
   * @param data : data being annotated
   * @throws AnnotatorException
   * @throws StreamException
   */
  public void create(PropertyBag properties, byte[] data) throws AnnotatorException, StreamException;
  public void mutate(PropertyBag properties, byte[] oldData, byte[] newData);
  public void transit(PropertyBag properties, byte[] data);
  public void close() throws StreamException;
}
