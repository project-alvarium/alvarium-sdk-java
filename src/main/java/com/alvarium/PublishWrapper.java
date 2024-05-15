
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

import com.alvarium.serializers.AlvariumPersistence;

import java.io.Serializable;

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
    return AlvariumPersistence.GSON.toJson(this);
  }
}
