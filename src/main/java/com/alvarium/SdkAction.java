package com.alvarium;

import com.google.gson.annotations.SerializedName;

/**
 * An identifier for the operations performed by the sdk
 */
public enum SdkAction {
  @SerializedName(value = "create")
  CREATE,
  @SerializedName(value = "mutate")
  MUTATE,
  @SerializedName(value = "transit")
  TRANSIT;
}
