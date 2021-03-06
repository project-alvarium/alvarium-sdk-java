
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
package com.alvarium.sign;

import java.io.Serializable;

import com.google.gson.Gson;

/**
 * a java bean that encapsulates the metadata related to a specific crypto key
 */
public class KeyInfo implements Serializable {
  private final String path;
  private final SignType type; 

  public KeyInfo(String path, SignType type) {
    this.path = path;
    this.type = type;
  }

  public String getPath() {
    return this.path;
  }

  public SignType getType() {
    return this.type;
  }

  public String toJson() {
    Gson gson = new Gson();
    return gson.toJson(this);
  }

  public static KeyInfo fromJson(String json) {
    Gson gson = new Gson();
    return gson.fromJson(json, KeyInfo.class);
  }
}
