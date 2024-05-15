package com.alvarium.serializers;

import com.alvarium.PublishWrapper;
import com.google.gson.*;

import java.lang.reflect.Type;

public class PublishWrapperConverter implements JsonSerializer<PublishWrapper> {


    @Override
    public JsonElement serialize(PublishWrapper src, Type typeOfSrc, JsonSerializationContext context) {
        var json = new JsonObject();
        json.add("action", new JsonPrimitive(src.getAction().name()));
        json.add("type", new JsonPrimitive(src.getMessageType()));
        json.add("content", context.serialize(src.getContent()));
        return json;
    }
}
