/*******************************************************************************
* Copyright 2024 Dell Inc.
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
package com.alvarium.annotators;

import java.io.Serializable;

import com.google.gson.Gson;

/**
 * A Java bean that holds the seed (data) and signature for this data
 */
class Signable implements Serializable {
  private final String seed;
  private final String signature;

  protected Signable(String seed, String signature) {
    this.seed = seed;
    this.signature = signature;
  }

  protected String getSeed() {
    return this.seed;
  }

  protected String getSignature() {
    return this.signature;
  }

  protected static Signable fromJson(String json) {
    final Gson gson = new Gson();
    return gson.fromJson(json, Signable.class);
  }

  protected String toJson() {
    final Gson gson = new Gson();
    return gson.toJson(this);
  }
}
