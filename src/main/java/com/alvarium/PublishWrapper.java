package com.alvarium;

import java.io.Serializable;

import com.google.gson.Gson;

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
    Gson gson = new Gson();
    return gson.fromJson(json, PublishWrapper.class);
  }

  public String toJson() {
    Gson gson = new Gson();
    return gson.toJson(this);
  }
}
