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
package com.alvarium.tag;

import com.alvarium.contracts.LayerType;
import java.util.Map;

public class TagManager {
    // TagEnvKey is an environment key used to associate annotations with specific metadata,
    // aiding in the linkage of scores across different layers of the stack. For instance, in the "app" layer,
    // it is utilized to retrieve the commit SHA of the workload where the application is running,
    // which is instrumental in tracing the impact on the current layer's score from the lower layers.
    private final String TAG_ENV_KEY = "TAG";
    private LayerType layer;

    public TagManager(LayerType layer){
        this.layer = layer;
    }

    private String defaultTagWriter(){
        switch(layer){
            case Application:
              return System.getenv(TAG_ENV_KEY) == null ? "" : System.getenv(TAG_ENV_KEY);
            default:
              break;
          }
          return "";
    }

    public String getTagValue(Map<LayerType, TagWriter> overrides){
        if (overrides != null && overrides.containsKey(layer)) {
            return overrides.get(layer).writeTag();
        } else {
            return defaultTagWriter();
        }
    }

    public String getTagValue(){
        return getTagValue(null);
    }
}