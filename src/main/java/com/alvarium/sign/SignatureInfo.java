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
package com.alvarium.sign;

import java.io.Serializable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * a java bean that encapsulates the private and public keys used in signature operations
 */
public class SignatureInfo implements Serializable {
  @SerializedName(value = "public", alternate = "publicKey")
  private final KeyInfo publicKey;

  @SerializedName(value = "private", alternate = "privateKey")
  private final KeyInfo privateKey;

  public SignatureInfo(KeyInfo publicKey, KeyInfo privateKey) {
    this.publicKey = publicKey;
    this.privateKey = privateKey;
  }

  public KeyInfo getPublicKey() {
    return this.publicKey;
  }

  public KeyInfo getPrivateKey() {
    return this.privateKey;
  }

  public String toJson() {
    Gson gson = new Gson();
    return gson.toJson(this);
  }

  public static SignatureInfo fromJson(String json) {
    Gson gson = new Gson();
    return gson.fromJson(json, SignatureInfo.class);
  }
}
