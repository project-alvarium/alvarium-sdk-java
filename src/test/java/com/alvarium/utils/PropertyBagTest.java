package com.alvarium.utils;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.Test;

public class PropertyBagTest {
  @Test
  public void getPropertyShouldGetValue() {
    final HashMap<String, Object> map = new HashMap<String,Object>();
    map.put("foo", "bar");
    final PropertyBag properties = new ImmutablePropertyBag(map);
    assertEquals(map.get("foo"), properties.getProperty("foo", String.class));
    assertEquals(map.get("foo").getClass(), properties.getProperty("foo", String.class).getClass());
  }

  @Test(expected = IllegalArgumentException.class)
  public void getPropertyShouldThrowException() throws IllegalArgumentException {
    final HashMap<String, Object> map = new HashMap<String,Object>();
    map.put("foo", "bar");
    final PropertyBag properties = new ImmutablePropertyBag(map);
    properties.getProperty("hello", String.class);

  }
}
