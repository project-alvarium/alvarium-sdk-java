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

import com.alvarium.PublishWrapper;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

/**
 * Responsible for connecting to an MQTT server and publishing messages to the provided topics
 */
public class MqttStreamProvider implements StreamProvider {
  private final IMqttClient client;
  final MqttConnectOptions options;
  private final MqttConfig endpoint;
  final int publishTimeoutSeconds = 2;
  final int waitOnCloseMs = 250;

  public MqttStreamProvider(MqttConfig cfg) throws StreamException {
    this.endpoint = cfg;
    this.options = new MqttConnectOptions();
    this.options.setUserName(cfg.getUser());
    this.options.setPassword(cfg.getPassword().toCharArray());
    this.options.setCleanSession(cfg.getIsClean());
    this.options.setConnectionTimeout(publishTimeoutSeconds);
    try {
      this.client = new MqttClient(cfg.getProvider().uri(), cfg.getClientId());
    } catch (IllegalArgumentException e) {
      throw new StreamException("Invalid uri or cliendId", e);
    } catch (MqttException e) {
      throw new StreamException("Could not instantiate client", e);
    }
  }

  public void connect() throws StreamException {
    this.reconnect();
  }

  public void close() throws StreamException {
    try {
      client.disconnect(waitOnCloseMs); // Blocking till disconnect completes
    } catch (MqttException e) {
      throw new StreamException("Could not close client", e);
    }
  }

  public void publish(PublishWrapper wrapper) throws StreamException {
    if (!client.isConnected()) {
      this.reconnect();
    }

    final String payload = wrapper.toJson();
    for (String topic : endpoint.getTopics()) {
      try {
        client.publish(topic, payload.getBytes(), endpoint.getQos(), false);
      } catch (MqttPersistenceException e) {
        throw new StreamException("Could not store message", e);
      } catch (IllegalArgumentException e) {
        throw new StreamException("Invalid QoS value", e);
      } catch (MqttException e) {
        throw new StreamException("Could not publish message", e);
      }
    }
  }

  private void reconnect() throws StreamException {
    if (!client.isConnected()) {
      try {
        client.connect(options);
      } catch (MqttSecurityException e) {
        throw new StreamException("Connection rejected: unsecure", e);
      } catch (MqttException e) {
        throw new StreamException("Connection failed", e);
      }
    }
  }
}
