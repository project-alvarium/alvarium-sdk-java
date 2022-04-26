
/*******************************************************************************
 * Copyright 2021 Dell Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
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
  @SerializedName(value = "pki-http")
  PKIHttp,
  @SerializedName(value = "src")
  SOURCE;
}
