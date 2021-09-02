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
    Gson gson = new GsonBuilder().registerTypeAdapter(StreamInfo.class, new StreamInfoConverter())
        .create();
    return gson.fromJson(json, StreamInfo.class);
    
  }
}
