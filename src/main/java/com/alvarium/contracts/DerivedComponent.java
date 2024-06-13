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
package com.alvarium.contracts;

import java.util.HashMap;
import java.util.Map;

public enum DerivedComponent {
  METHOD("@method"),
  TARGETURI("@target-uri"),
  AUTHORITY("@authority"),
  SCHEME("@scheme"),
  PATH("@path"),
  QUERY("@query"),
  QUERYPARAMS("@query-params");

  private static final Map<String, DerivedComponent> derivedComponentMap = new HashMap<>();
  private final String value;

  private DerivedComponent(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }

  static {
    for (DerivedComponent derivedComponent : DerivedComponent.values()) {
      derivedComponentMap.put(derivedComponent.value, derivedComponent);
    }
  }

  public static DerivedComponent fromString(String value) throws EnumConstantNotPresentException {
    DerivedComponent derivedComponent = derivedComponentMap.get(value);
    if (derivedComponent != null) {
      return derivedComponent;
    }
    throw new EnumConstantNotPresentException(DerivedComponent.class, value);
  }
}
