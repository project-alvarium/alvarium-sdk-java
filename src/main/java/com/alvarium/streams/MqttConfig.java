package com.alvarium.streams;

import java.io.Serializable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Holds the configuration required to connect to an MQTT server and publish messages
 */
public class MqttConfig  implements Serializable {
  private final String clientId;
  private final String user;
  private final String password;
  private final int qos;
  @SerializedName(value = "cleanness", alternate = "isClean")
  private final boolean isClean;
  private final String[] topics;
  private final ServiceInfo provider;

  public MqttConfig(
        String cliendId,
        String user, 
        String password, 
        int qos, 
        boolean isClean, 
        String[] topics,
        ServiceInfo provider
    ) {
    this.clientId = cliendId;
    this.user = user;
    this.password= password;
    this.qos = qos;
    this.isClean = isClean;
    this.topics = topics;
    this.provider = provider;
  }

  public String getClientId() {
    return this.clientId;
  }

  public String getUser() {
    return this.user;
  }

  public String getPassword() {
    return this.password;
  }
  
  public int getQos() {
    return this.qos;
  }

  public boolean getIsClean() {
    return this.isClean;
  }

  public String[] getTopics() {
    return this.topics;
  }

  public ServiceInfo getProvider() {
    return this.provider;
  }

  
  public String toJson() {
    Gson gson = new Gson();
    return gson.toJson(this);
  }

  public static MqttConfig fromJson(String json) {
    Gson gson = new Gson();
    return gson.fromJson(json, MqttConfig.class);
  }

}
