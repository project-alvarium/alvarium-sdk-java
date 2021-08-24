package com.alvarium.serializers;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * A converter of Instant datatype to RFC3339 string representation and vice versa
 */
public class InstantConverter implements JsonSerializer<Instant>, JsonDeserializer<Instant>{

  public JsonElement serialize(Instant src, Type typeOfSrc, JsonSerializationContext context) {
    // a workaround to preserve the zone information 
    DateTimeFormatter f = DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault());
    ZonedDateTime zdt = ZonedDateTime.parse(DateTimeFormatter.ISO_INSTANT.format(src), f);
    String raw = zdt.toString();

    if (raw.indexOf('[') > 0) {
      return new JsonPrimitive(zdt.toString().substring(0, raw.indexOf('[')));
    }
    return new JsonPrimitive(raw);
  }

  @Override
  public Instant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return DateTimeFormatter.ISO_ZONED_DATE_TIME.parse(json.getAsString(), Instant::from);
  }
}
