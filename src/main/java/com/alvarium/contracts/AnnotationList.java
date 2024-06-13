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

import java.util.List;

import com.alvarium.serializers.AnnotationConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * A wrapper over the list of annotations
 */
public class AnnotationList {
  private final List<Annotation> items;

  public AnnotationList(Annotation[] annotations) {
    this.items = List.of(annotations);
  }

  public AnnotationList(List<Annotation> annotations) {
    this.items = annotations;
  }

  public List<Annotation> getAnnotations() {
    return this.items;
  }

  public String toJson() {
    Gson gson =
        new GsonBuilder()
            .registerTypeAdapter(Annotation.class, new AnnotationConverter())
            .create();

    return gson.toJson(this);
  }

  public static AnnotationList fromJson(String json) {
    Gson gson =
        new GsonBuilder()
            .registerTypeAdapter(Annotation.class, new AnnotationConverter())
            .create();

    return gson.fromJson(json, AnnotationList.class);
  }
}
