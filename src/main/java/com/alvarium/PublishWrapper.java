package com.alvarium;

import java.io.Serializable;

import com.alvarium.contracts.Annotation;
import com.alvarium.serializers.AnnotationConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * A java bean that encapsulates the content sent through the stream providers
 * and appends the required metadata
 */
public class PublishWrapper implements Serializable {
  private final SdkAction action;
  private final String messageType;
  private final Object content;

  public PublishWrapper(SdkAction action, String messageType, Object content) {
    this.action = action;
    this.messageType = messageType;
    this.content = content;
  }

  public SdkAction getAction() {
    return action;
  }

  public String getMessageType() {
    return messageType;
  }

  public Object getContent() {
    return content;
  }

  public static PublishWrapper fromJson(String json) {
    Gson gson = new GsonBuilder().registerTypeAdapter(Annotation.class, new AnnotationConverter())
        .create();
    return gson.fromJson(json, PublishWrapper.class);
  }

  public String toJson() {
    Gson gson = new GsonBuilder().registerTypeAdapter(Annotation.class, new AnnotationConverter())
        .create();
    return gson.toJson(this);
  }
}
