package com.alvarium.hash;

import com.google.gson.annotations.SerializedName;

public enum HashType {
  @SerializedName(value = "none")
  NoHash,
  @SerializedName(value = "md5")
  MD5Hash,
  @SerializedName(value = "sha256")
  SHA256Hash;
}
