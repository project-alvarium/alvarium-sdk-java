package com.alvarium.serializers;

import com.alvarium.PublishWrapper;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Base64;

public class PublishWrapperConverter implements JsonSerializer<PublishWrapper> {


    @Override
    public JsonElement serialize(PublishWrapper src, Type typeOfSrc, JsonSerializationContext context) {
        var json = new JsonObject();
        json.add("action", new JsonPrimitive(src.getAction().name()));
        json.add("type", new JsonPrimitive(src.getMessageType()));

        String contentJsonString = context.serialize(src.getContent()).toString();

        json.add("content", new JsonPrimitive(Base64.getEncoder().encodeToString(contentJsonString.getBytes())));
        return json;
    }
}
