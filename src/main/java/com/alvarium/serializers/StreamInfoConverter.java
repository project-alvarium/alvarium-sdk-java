package com.alvarium.serializers;

import java.lang.reflect.Type;

import com.alvarium.streams.MqttConfig;
import com.alvarium.streams.StreamInfo;
import com.alvarium.streams.StreamType;
import com.google.gson.JsonDeserializer;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
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
        JsonElement json, 
        Type typeOfT, 
        JsonDeserializationContext context
    ) {
    JsonObject obj = json.getAsJsonObject();
    StreamType type = StreamType.valueOf(obj.get("type").getAsString());
    switch(type){
      case MQTT: 
        MqttConfig config = MqttConfig.fromJson(obj.get("config").toString());
        return new StreamInfo(type,config);
      default: 
        Gson gson = new Gson();
        return gson.fromJson(json, StreamInfo.class);
    } 
  }
}
