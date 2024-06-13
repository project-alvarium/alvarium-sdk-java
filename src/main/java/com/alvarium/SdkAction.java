/*******************************************************************************
* Copyright 2024 Dell Inc.
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
  TRANSIT,
  @SerializedName(value = "publish")
  PUBLISH;
}
