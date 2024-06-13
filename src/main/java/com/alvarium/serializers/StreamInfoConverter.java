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
package com.alvarium.serializers;

import java.lang.reflect.Type;

import com.alvarium.streams.MqttConfig;
import com.alvarium.streams.PravegaConfig;
import com.alvarium.streams.StreamInfo;
import com.alvarium.streams.StreamType;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * <p>Contains the logic for deserializing stream configs depending on their types</p>
 *
 * <p>Any further stream implementations must have their config deserialization logic here</p>
 *
 * <p>This is because the StreamInfo class must be able to pass down any different type of stream
 * config class (e.g. IotaConfig, MqttConfig, PravegaConfig).</p>
 */
public class StreamInfoConverter implements JsonDeserializer<StreamInfo> {
  public StreamInfo deserialize(
      JsonElement json, Type typeOfT, JsonDeserializationContext context) {
    Gson gson = new Gson();
    JsonObject obj = json.getAsJsonObject();
    StreamType type = gson.fromJson(obj.get("type"), StreamType.class);
    switch (type) {
      case MQTT:
        MqttConfig mqttConfig = MqttConfig.fromJson(obj.get("config").toString());
        return new StreamInfo(type, mqttConfig);
      case PRAVEGA:
        PravegaConfig pravegaConfig = PravegaConfig.fromJson(obj.get("config").toString());
        return new StreamInfo(type, pravegaConfig);
      default:
        return gson.fromJson(json, StreamInfo.class);
    }
  }
}
