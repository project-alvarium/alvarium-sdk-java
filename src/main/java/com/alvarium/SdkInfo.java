
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
package com.alvarium;

import java.io.Serializable;

import com.alvarium.contracts.AnnotationType;
import com.alvarium.hash.HashInfo;
import com.alvarium.serializers.StreamInfoConverter;
import com.alvarium.sign.SignatureInfo;
import com.alvarium.streams.StreamInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * A java bean that encapsulates sdk related configuration
 */
public class SdkInfo implements Serializable {
  private final AnnotationType[] annotators;
  private final HashInfo hash;
  private final SignatureInfo signature;
  private final StreamInfo stream;

  public SdkInfo(AnnotationType[] annotators, HashInfo hash, SignatureInfo signature,
      StreamInfo stream) {
    this.annotators = annotators;
    this.hash = hash;
    this.signature = signature;
    this.stream = stream;
  }

  public AnnotationType[] getAnnotators() {
    return this.annotators;
  }

  public HashInfo getHash() {
    return this.hash;
  }

  public SignatureInfo getSignature() {
    return this.signature;
  }

  public StreamInfo getStream() {
    return this.stream;
  }

  public String toJson() {
    Gson gson = new GsonBuilder().registerTypeAdapter(StreamInfo.class, new StreamInfoConverter())
        .create();
    return gson.toJson(this);
  }

  public static SdkInfo fromJson(String json) {
    Gson gson = new GsonBuilder().registerTypeAdapter(StreamInfo.class, new StreamInfoConverter())
        .create();
    return gson.fromJson(json, SdkInfo.class);
  }
}
