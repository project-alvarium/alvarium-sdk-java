
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
package com.alvarium.streams;

import java.io.Serializable;

import com.google.gson.Gson;

/**
 * Holds the necessary information to connect to a service (e.g. Iota, MQTT)
 */
public class ServiceInfo implements Serializable {
  private final String host;
  private final String protocol;
  private final int port;

  protected ServiceInfo(String host, String protocol, int port) {
    this.host = host;
    this.protocol = protocol;
    this.port = port;
  }

  /**
   * Creates the uri for the service
   * @return uri as `protocol://host:port`
   */
  public String uri() {
    return String.format("%s://%s:%d", protocol, host, port);
  }  

  public String toJson() {
    Gson gson = new Gson();
    return gson.toJson(this);
  }

  public static ServiceInfo fromJson(String json) {
    Gson gson = new Gson();
    return gson.fromJson(json, ServiceInfo.class);
  }
}
