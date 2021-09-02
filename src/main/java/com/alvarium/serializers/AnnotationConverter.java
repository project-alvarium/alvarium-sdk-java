package com.alvarium.serializers;

import java.lang.reflect.Type;

import com.alvarium.contracts.Annotation;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;


public class AnnotationConverter implements JsonSerializer<Annotation>, JsonDeserializer<Annotation> {

  public JsonElement serialize(Annotation src, Type typeOfSrc, JsonSerializationContext context) {
    return JsonParser.parseString(src.toJson()); 
  }

  public Annotation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
    return Annotation.fromJson(json.getAsString());
  }
}
