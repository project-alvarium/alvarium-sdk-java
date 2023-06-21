
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

import java.lang.reflect.Type;


import com.alvarium.annotators.AnnotatorConfig;
import com.alvarium.annotators.MockAnnotatorConfig;
import com.alvarium.contracts.AnnotationType;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class AnnotatorConfigConverter implements JsonDeserializer<AnnotatorConfig> {
    
    public AnnotatorConfig deserialize(
            JsonElement json, 
            Type typeOfT, 
            JsonDeserializationContext context
    ) {
        JsonObject obj = json.getAsJsonObject();
        if (!obj.has("kind")) {
            throw new IllegalArgumentException("Annotator config entry does not specify kind");
        }
        Gson gson = new Gson();
        AnnotationType kind = gson.fromJson(obj.getAsJsonPrimitive("kind"), AnnotationType.class);
        if (kind == null) {
            throw new IllegalArgumentException("Invalid annotator kind provided in config");
        }
        switch (kind) {
            case MOCK:
                MockAnnotatorConfig mockCfg = gson.fromJson(obj.toString(), MockAnnotatorConfig.class);
                return mockCfg;
            default: 
                return gson.fromJson(json, AnnotatorConfig.class);
        }
    }
}

