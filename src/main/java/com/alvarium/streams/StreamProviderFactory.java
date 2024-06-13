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
package com.alvarium.streams;

/**
 * A factory that provides different implementations of the StreamProvider interface
 */
public class StreamProviderFactory {
  public StreamProvider getProvider(StreamInfo info) throws StreamException {
    switch (info.getType()) {
      case MQTT:
        try {
          MqttConfig config = MqttConfig.class.cast(info.getConfig());
          return new MqttStreamProvider(config);
        } catch (ClassCastException e) {
          throw new StreamException("Invalid stream config", e);
        }
      case PRAVEGA:
        try {
          PravegaConfig config = PravegaConfig.class.cast(info.getConfig());
          return new PravegaStreamProvider(config);
        } catch (ClassCastException e) {
          throw new StreamException("Invalid stream config", e);
        }
      case MOCK:
        return new MockStreamProvider();
      default:
        throw new StreamException(String.format("%s is not supported", info.getType()));
    }
  }
}
