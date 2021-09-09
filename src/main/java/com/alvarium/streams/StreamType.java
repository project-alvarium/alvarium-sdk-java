package com.alvarium.streams;

import com.google.gson.annotations.SerializedName;

/**
 * An identifier for the stream used by the sdk
 */
public enum StreamType {
  @SerializedName(value = "mqtt")
  MQTT,
  @SerializedName(value = "mock")
  MOCK;
}
