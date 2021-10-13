
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
package com.alvarium;

import java.io.Serializable;
import java.util.Base64;

import com.alvarium.contracts.Annotation;
import com.alvarium.serializers.AnnotationConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

/**
 * A java bean that encapsulates the content sent through the stream providers
 * and appends the required metadata
 */
public class PublishWrapper implements Serializable {
  private final SdkAction action;
  private final String messageType;
  private final Object content;

  public PublishWrapper(SdkAction action, String messageType, Object content) {
    this.action = action;
    this.messageType = messageType;
    this.content = content;
  }

  public SdkAction getAction() {
    return action;
  }

  public String getMessageType() {
    return messageType;
  }

  public Object getContent() {
    return content;
  }

  /**
   * The content field in the returned JSON will be Base64 string encoded 
   * @return String representation of the PublishWrapper JSON
   */
  public String toJson() {
    Gson gson = new GsonBuilder()
        .registerTypeAdapter(Annotation.class, new AnnotationConverter())
        .disableHtmlEscaping()
        .create();
    
    // Change the content field to a base64 encoded string before serializing to json
    final JsonElement decodedContent = gson.toJsonTree(this.content);
    final String encodedContent;
    
    // `toString()` will work if the content is a primitive type, but will add additional 
    // quotes (e.g. "foo" will be "\"foo\"") but `getAsString()` will produce correct behavior but
    // using `getAsString()` on a non-primitive type will throw an exception.
    // This condition ensures that the correct method is called on the correct type
    if (decodedContent.isJsonPrimitive()) {
      encodedContent = Base64.getEncoder().encodeToString(decodedContent.getAsString().getBytes());
    } else {
      encodedContent = Base64.getEncoder().encodeToString(decodedContent.toString().getBytes());
    }

    // new publish wrapper returned as JSON string with encoded content 
    // to prevent setting the object content value
    final PublishWrapper wrapper = new PublishWrapper(action, messageType, encodedContent);
    return gson.toJson(wrapper);
  }
}
