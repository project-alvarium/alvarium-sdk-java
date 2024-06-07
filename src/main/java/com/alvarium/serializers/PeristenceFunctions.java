package com.alvarium.serializers;

import com.alvarium.PublishWrapper;
import com.alvarium.contracts.Annotation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.ZonedDateTime;

public class PeristenceFunctions {


  private static final Gson GSON = new GsonBuilder()
      .registerTypeAdapter(PublishWrapper.class, new PublishWrapperConverter())
      .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeConverter())
      .registerTypeAdapter(Annotation.class, new AnnotationConverter())
      .disableHtmlEscaping()
      .create();

  public static String serializeWrapper(PublishWrapper wrapper) {
    return GSON.toJson(wrapper);
  }

  public static String serializeAnnotation(Annotation annotation) {
    return GSON.toJson(annotation);
  }

  public static Annotation deserializeAnnotation(String jsonAnnotation) {
    return GSON.fromJson(jsonAnnotation, Annotation.class);
  }

}
