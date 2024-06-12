package com.alvarium.serializers;

import com.alvarium.contracts.AnnotationList;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class AnnotationListConverter implements JsonSerializer<AnnotationList> {
  @Override
  public JsonElement serialize(AnnotationList src, Type typeOfSrc, JsonSerializationContext context) {
    return context.serialize(src.getAnnotations());
  }
}
