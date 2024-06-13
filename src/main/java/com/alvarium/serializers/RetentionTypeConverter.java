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
package com.alvarium.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import io.pravega.client.stream.RetentionPolicy.RetentionType;

/**
 * An adapter used to convert RetentionType enum to it's lowercase representation and vice versa.
 * It is used to to properly load config from json.
 */
public class RetentionTypeConverter
    implements JsonSerializer<RetentionType>, JsonDeserializer<RetentionType> {

  public JsonElement serialize(
      RetentionType src, Type typeOfSrc, JsonSerializationContext context) {
    return new JsonPrimitive(src.name().toLowerCase());
  }

  public RetentionType deserialize(
      JsonElement json, Type typeOfT, JsonDeserializationContext context) {
    return RetentionType.valueOf(json.getAsString().toUpperCase());
  }
}
