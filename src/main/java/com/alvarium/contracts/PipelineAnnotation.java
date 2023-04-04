package com.alvarium.contracts;

import java.time.Instant;

import com.alvarium.hash.HashType;
import com.alvarium.serializers.InstantConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PipelineAnnotation extends Annotation {

    private final String pipelineId;

    public PipelineAnnotation(String key, HashType hash, String host, AnnotationType kind, String signature,
            Boolean isSatisfied, Instant timestamp, String pipelineId) {
        super(key, hash, host, kind, signature, isSatisfied, timestamp);
        this.pipelineId = pipelineId;
    }

    // getters
    public String getPipelineId() {
        return this.pipelineId;
    }

    @Override
    public String toJson() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new InstantConverter())
                .create();
        return gson.toJson(this, PipelineAnnotation.class);
    }
}
