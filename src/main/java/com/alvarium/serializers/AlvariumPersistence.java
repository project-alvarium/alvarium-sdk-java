package com.alvarium.serializers;

import com.alvarium.PublishWrapper;
import com.alvarium.contracts.Annotation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.ZonedDateTime;

public class AlvariumPersistence {


    public static final Gson GSON = new GsonBuilder()
        .registerTypeAdapter(PublishWrapper.class, new PublishWrapperConverter())
        .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeConverter())
        .registerTypeAdapter(Annotation.class, new AnnotationConverter())
        .disableHtmlEscaping()
        .create();

}
