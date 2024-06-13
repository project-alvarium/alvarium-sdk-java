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
package com.alvarium.streams;

import java.io.Serializable;

import com.alvarium.serializers.StreamInfoConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Contains the type of stream being used as well as the relevant configuration
 */
public class StreamInfo implements Serializable {
  private final StreamType type;
  private final Object config;

  public StreamInfo(StreamType type, Object config) {
    this.type = type;
    this.config = config;
  }

  public StreamType getType() {
    return this.type;
  }

  public Object getConfig() {
    return this.config;
  }

  public String toJson() {
    Gson gson = new Gson();
    return gson.toJson(this);
  }

  public static StreamInfo fromJson(String json) {
    Gson gson =
        new GsonBuilder()
            .registerTypeAdapter(StreamInfo.class, new StreamInfoConverter())
            .create();
    return gson.fromJson(json, StreamInfo.class);
  }
}
