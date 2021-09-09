package com.alvarium;

import java.io.Serializable;

import com.alvarium.contracts.AnnotationType;
import com.alvarium.hash.HashInfo;
import com.alvarium.serializers.StreamInfoConverter;
import com.alvarium.sign.SignatureInfo;
import com.alvarium.streams.StreamInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * A java bean that encapsulates sdk related configuration
 */
public class SdkInfo implements Serializable {
  private final AnnotationType[] annotators;
  private final HashInfo hash;
  private final SignatureInfo signature;
  private final StreamInfo stream;

  public SdkInfo(AnnotationType[] annotators, HashInfo hash, SignatureInfo signature,
      StreamInfo stream) {
    this.annotators = annotators;
    this.hash = hash;
    this.signature = signature;
    this.stream = stream;
  }

  public AnnotationType[] getAnnotators() {
    return this.annotators;
  }

  public HashInfo getHash() {
    return this.hash;
  }

  public SignatureInfo getSignature() {
    return this.signature;
  }

  public StreamInfo getStream() {
    return this.stream;
  }

  public String toJson() {
    Gson gson = new GsonBuilder().registerTypeAdapter(StreamInfo.class, new StreamInfoConverter())
        .create();
    return gson.toJson(this);
  }

  public static SdkInfo fromJson(String json) {
    Gson gson = new GsonBuilder().registerTypeAdapter(StreamInfo.class, new StreamInfoConverter())
        .create();
    return gson.fromJson(json, SdkInfo.class);
  }
}
