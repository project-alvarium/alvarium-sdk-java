package com.alvarium.utils;

import java.util.HashMap;
import java.util.Map;

public class ImmutablePropertyBag implements PropertyBag{
  private final HashMap<String,Object> bag;

  public ImmutablePropertyBag(Map<String,Object> map) {
    this.bag = new HashMap<String,Object>(map);
  }

  public <T> T getProperty(String key, Class<T> c) {
    if(bag.containsKey(key)) {
        return c.cast(bag.get(key));
    } else {
      throw new IllegalArgumentException(String.format("Property %s not found", key));
    }
  }
  
  public Map<String, Object> toMap() {
    return bag;
  }
}
