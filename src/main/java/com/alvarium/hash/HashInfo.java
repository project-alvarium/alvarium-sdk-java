package com.alvarium.hash;

import java.io.Serializable;

import com.google.gson.Gson;

/**
 * A java bean that encapsulates all data related to hash providers
 */
public class HashInfo implements Serializable {
  private final HashType type;

  public HashInfo(HashType type) {
    this.type = type;
  }

  public HashType getType(){
    return this.type;
  }

  public String toJson() {
    Gson gson = new Gson();
    return gson.toJson(this);
  }

  public static HashInfo fromJson(String json) {
    Gson gson = new Gson();
    return gson.fromJson(json, HashInfo.class);
  }
}
