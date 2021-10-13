
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
package com.alvarium.serializers;

import java.lang.reflect.Type;

import com.alvarium.contracts.Annotation;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;


public class AnnotationConverter implements JsonSerializer<Annotation>, JsonDeserializer<Annotation> {

  public JsonElement serialize(Annotation src, Type typeOfSrc, JsonSerializationContext context) {
    return JsonParser.parseString(src.toJson()); 
  }

  public Annotation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
    return Annotation.fromJson(json.toString());
  }
}
