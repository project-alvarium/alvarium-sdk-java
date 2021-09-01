package com.alvarium.sign;

import java.io.Serializable;

import com.google.gson.Gson;

/**
 * a java bean that encapsulates the metadata related to a specific crypto key
 */
public class KeyInfo implements Serializable {
  private final String path;
  private final SignType type; 

  public KeyInfo(String path, SignType type) {
    this.path = path;
    this.type = type;
  }

  public String getPath() {
    return this.path;
  }

  public SignType getType() {
    return this.type;
  }

  public String toJson() {
    Gson gson = new Gson();
    return gson.toJson(this);
  }

  public static KeyInfo fromJson(String json) {
    Gson gson = new Gson();
    return gson.fromJson(json, KeyInfo.class);
  }
}
