package com.alvarium.serializers;

import com.alvarium.PublishWrapper;
import com.alvarium.contracts.Annotation;
import com.alvarium.contracts.AnnotationList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.ZonedDateTime;

public class Serialization {


  private static final Gson GSON = new GsonBuilder()
      .registerTypeAdapter(PublishWrapper.class, new PublishWrapperConverter())
      .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeConverter())
      .registerTypeAdapter(Annotation.class, new AnnotationConverter())
      .registerTypeAdapter(AnnotationList.class, new AnnotationListConverter())
      .disableHtmlEscaping()
      .create();

  public static String toJson(PublishWrapper wrapper) {
    return GSON.toJson(wrapper);
  }

  public static String toJson(Annotation annotation) {
    return GSON.toJson(annotation);
  }

  public static String toJson(AnnotationList list) {
    return GSON.toJson(list);
  }


  public static Annotation annotationFromJson(String jsonAnnotation) {
    return GSON.fromJson(jsonAnnotation, Annotation.class);
  }

  public static AnnotationList annotationListFromJson(String jsonAnnotation) {
    return GSON.fromJson(jsonAnnotation, AnnotationList.class);
  }

}
