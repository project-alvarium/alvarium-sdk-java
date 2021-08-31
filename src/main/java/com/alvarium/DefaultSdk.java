package com.alvarium;

import com.alvarium.utils.PropertyBag;

public class DefaultSdk implements Sdk {

  DefaultSdk() {
    this.init();
  }

  private void init() {
  }

  public void create(PropertyBag properties, byte[] data) {
  }

  public void mutate(PropertyBag properties, byte[] oldData, byte[] newData) {
  }

  public void transit(PropertyBag properties, byte[] data) {
  }

  public void close() {
  }
}
