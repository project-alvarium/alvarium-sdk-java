
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
package com.alvarium.hash;

import java.io.Serializable;

import com.google.gson.Gson;

/**
 * A java bean that encapsulates all data related to hash providers
 */
public class HashInfo implements Serializable {
  private final HashType type;

  public HashInfo(HashType type) {
    this.type = type;
  }

  public HashType getType(){
    return this.type;
  }

  public String toJson() {
    Gson gson = new Gson();
    return gson.toJson(this);
  }

  public static HashInfo fromJson(String json) {
    Gson gson = new Gson();
    return gson.fromJson(json, HashInfo.class);
  }
}
