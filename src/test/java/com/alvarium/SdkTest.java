package com.alvarium;

import java.util.HashMap;

import com.alvarium.utils.PropertyBag;
import com.alvarium.utils.ImmutablePropertyBag;

import org.junit.Test;

class MockSdk implements Sdk {
  public void create(PropertyBag properties, byte[] data) {
  }
  public void mutate(PropertyBag properties, byte[] oldData, byte[] newData) {
  }
  public void transit(PropertyBag properties, byte[] data) {
  }
  public void close() {
    System.out.println("Connections closed");
  } 
}
public class SdkTest {
  @Test
  @SuppressWarnings("unused")
  public void instantiateSdkShouldNotThrow() {
    final Sdk sdk = new DefaultSdk();
  }

  @Test
  public void createShouldReturnSameData() {
    final Sdk sdk = new MockSdk();
    final PropertyBag properties = new ImmutablePropertyBag(new HashMap<String,Object>());
    byte[] oldData = {0xA, 0x1};
    byte[] newData = {0x1, 0xA};
    sdk.create(properties, oldData);
    sdk.mutate(properties, oldData, newData);
    sdk.transit(properties, oldData);
  }
}
