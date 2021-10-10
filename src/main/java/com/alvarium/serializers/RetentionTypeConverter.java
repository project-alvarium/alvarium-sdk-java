package com.alvarium.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import io.pravega.client.stream.RetentionPolicy.RetentionType;

/**
 * An adapter used to convert RetentionType enum to it's lowercase representation and vice versa.
 * It is used to to properly load config from json.
 */
public class RetentionTypeConverter implements JsonSerializer<RetentionType>,
    JsonDeserializer<RetentionType> {

  public JsonElement serialize(RetentionType src, Type typeOfSrc, JsonSerializationContext context) {
    return new JsonPrimitive(src.name().toLowerCase()); 
  }

  public RetentionType deserialize(JsonElement json, Type typeOfT,
      JsonDeserializationContext context) {
    return RetentionType.valueOf(json.getAsString().toUpperCase()); 
  } 
}