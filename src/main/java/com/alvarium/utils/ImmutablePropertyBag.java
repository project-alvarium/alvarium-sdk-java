
/*******************************************************************************
 * Copyright 2021 Dell Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
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

  public boolean hasProperty(String key) {
    return bag.containsKey(key);
  }
  
  public Map<String, Object> toMap() {
    return bag;
  }
}