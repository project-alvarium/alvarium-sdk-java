package com.alvarium.utils;

import java.util.Map;

public interface PropertyBag {
  public <T> T getProperty(String key, Class<T> c);

  public Map<String, Object> toMap();
}
