package com.alvarium.annotators;

import java.io.Serializable;

import com.google.gson.Gson;

/**
 * A Java bean that holds the seed (data) and signature for this data
 */
class Signable implements Serializable {
  private final String seed;
  private final String signature;

  protected Signable(String seed, String signature) {
    this.seed = seed;
    this.signature = signature;
  }

  protected String getSeed() {
    return this.seed;
  }

  protected String getSignature() {
    return this.signature;
  }

  protected static Signable fromJson(String json) {
    final Gson gson = new Gson();
    return gson.fromJson(json, Signable.class);
  }

  protected String toJson() {
    final Gson gson = new Gson();
    return gson.toJson(this);
  }
}
