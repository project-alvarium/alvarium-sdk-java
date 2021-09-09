package com.alvarium.sign;

import com.google.gson.annotations.SerializedName;

public enum SignType {
  @SerializedName(value = "ed25519")
  Ed25519,
  @SerializedName(value = "none")
  none;
 }