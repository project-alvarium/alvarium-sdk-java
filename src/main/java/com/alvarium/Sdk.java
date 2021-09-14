package com.alvarium;

import com.alvarium.streams.StreamException;
import com.alvarium.utils.PropertyBag;

public interface Sdk {
  
  public void create(PropertyBag properties, byte[] data);
  public void mutate(PropertyBag properties, byte[] oldData, byte[] newData);
  public void transit(PropertyBag properties, byte[] data);
  public void close() throws StreamException;
}
