/*******************************************************************************
 * Copyright 2023 Dell Inc.
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
package com.alvarium.serializers;

import com.alvarium.contracts.Annotation;
import com.google.gson.*;

import java.lang.reflect.Type;

public class AnnotationConverter implements JsonSerializer<Annotation> {

  @Override
  public JsonElement serialize(Annotation src, Type typeOfSrc, JsonSerializationContext context) {
    var json = new JsonObject();

    json.add("id", new JsonPrimitive(src.getId()));
    json.add("type", new JsonPrimitive(src.getKind().name()));
    json.add("tag", new JsonPrimitive(src.getTag()));
    json.add("hash", new JsonPrimitive(src.getHash().name()));
    json.add("host", new JsonPrimitive(src.getHost()));
    json.add("layer", new JsonPrimitive(src.getLayer().name()));
    json.add("signature", src.getSignature() == null ? null : new JsonPrimitive(src.getSignature()));
    json.add("timestamp", context.serialize(src.getTimestamp()));
    json.add("isSatisfied", new JsonPrimitive(src.getIsSatisfied()));

    return json;
  }
}
