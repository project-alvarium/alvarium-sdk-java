
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

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A converter of Instant datatype to RFC3339 string representation and vice versa
 */
public class ZonedDateTimeConverter implements JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {


    public JsonElement serialize(ZonedDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(src.getZone());
        return new JsonPrimitive(formatter.format(src));
    }

    @Override
    public ZonedDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(json.getAsString(), ZonedDateTime::from);
    }
}
