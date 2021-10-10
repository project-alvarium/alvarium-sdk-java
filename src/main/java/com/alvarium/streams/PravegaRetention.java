package com.alvarium.streams;

import java.io.Serializable;

import com.alvarium.serializers.RetentionTypeConverter;
import com.google.gson.Gson;
import com.google.gson.annotations.JsonAdapter;

import io.pravega.client.stream.RetentionPolicy.RetentionType;

/**
 * A unit that encapsulates the Pravega retention policy configuration.
 * More info about retention policy can be found here:
 * https://pravega.io/docs/v0.6.0/pravega-concepts/#stream-retention-policies
 */
class PravegaRetention implements Serializable {
  @JsonAdapter(RetentionTypeConverter.class)
  private final RetentionType type;
  private final long min; 
  private final long max; 

  public PravegaRetention(RetentionType type, long min, long max){
    this.type = type;
    this.min = min;
    this.max = max;
  }

  public RetentionType getType(){
    return this.type;
  }

  public long getMin(){
    return this.min;
  }

  public long getMax(){
    return this.max;
  }

  public String toJson(){
    Gson gson = new Gson();
    return gson.toJson(this);
  }

  public static PravegaRetention fromJson(String json){
    Gson gson = new Gson();
    return gson.fromJson(json, PravegaRetention.class);
  }
}
