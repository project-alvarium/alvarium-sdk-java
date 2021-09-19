package com.alvarium.contracts;

import com.google.gson.annotations.SerializedName;

public enum AnnotationType {
  @SerializedName(value = "tpm")
  TPM, 
  @SerializedName(value = "mock")
  MOCK,
  @SerializedName(value = "tls")
  TLS,
  @SerializedName(value = "pki")
  PKI,
  @SerializedName(value = "source")
  SOURCE;
}
