package com.alvarium.sign;

import java.io.Serializable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * a java bean that encapsulates the private and public keys used in signature operations
 */
public class SignatureInfo implements Serializable {
  @SerializedName(value = "public", alternate = "publicKey")
  private final KeyInfo publicKey;  
  @SerializedName(value = "private", alternate = "privateKey")
  private final KeyInfo privateKey;

  public SignatureInfo(KeyInfo publicKey, KeyInfo privateKey) {
    this.publicKey = publicKey;
    this.privateKey = privateKey;
  }

  public KeyInfo getPublicKey() {
    return this.publicKey;
  }

  public KeyInfo getPrivateKey() {
    return this.privateKey;
  }

  public String toJson() {
    Gson gson = new Gson();
    return gson.toJson(this);
  }

  public static SignatureInfo fromJson(String json) {
    Gson gson = new Gson();
    return gson.fromJson(json, SignatureInfo.class);
  }
}
