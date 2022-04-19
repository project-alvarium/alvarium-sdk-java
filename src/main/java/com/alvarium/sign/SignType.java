
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

import java.util.Map;
import java.util.HashMap;

import com.google.gson.annotations.SerializedName;

public enum SignType {
  @SerializedName(value = "ed25519")
  Ed25519("ed25519"),
  @SerializedName(value = "none")
  none("none");

  private static final Map<String, SignType> signTypeMap = new HashMap<>();
  private final String value;

  private SignType(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }

  static {
    for (SignType signType : SignType.values()) {
      signTypeMap.put(signType.value, signType);
    }
  }

  public static SignType fromString(String value) throws EnumConstantNotPresentException {
    SignType signType = signTypeMap.get(value);
    if (signType != null) {
      return signType;
    }
    throw new EnumConstantNotPresentException(SignType.class, value);
  }
}