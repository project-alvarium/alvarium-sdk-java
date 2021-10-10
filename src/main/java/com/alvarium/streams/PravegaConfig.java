package com.alvarium.streams;

import java.io.Serializable;

import com.google.gson.Gson;

/**
 * A unit that encapsulates all of the important pravega related configuration.
 * Most of the important fields are grabbed from here: 
 * https://pravega.io/docs/v0.6.0/basic-reader-and-writer/#creating-a-stream-and-the-streammanager-interface
 */
public class PravegaConfig implements Serializable {
  /**
   * A field that determines how a Stream handles the varying changes in its load. 
   */
  private final Integer scalingPolicy;
  /**
   * The name of the scope under which the selected stream will live.
   */
  private final String scope; 
  /**
   * The name of the stream under the chosen scope, which events will be written to.
   */
  private final String stream; 
  /**
   * Used to group certain events together.
   */
  private final String routingKey;
  private final PravegaRetention retention;
  private final ServiceInfo provider;

  public PravegaConfig(
    Integer scalingPolicy, 
    ServiceInfo provider,
    String scope, 
    String stream, 
    String routingKey,
    PravegaRetention retention) {
    this.scalingPolicy = scalingPolicy;
    this.provider = provider;
    this.scope = scope;
    this.stream = stream;
    this.routingKey = routingKey;
    this.retention = retention;
  }

  public Integer getScalingPolicy() {
    return this.scalingPolicy;
  }

  public ServiceInfo getProvider() {
    return this.provider;
  }

  public String getScope() {
    return this.scope;
  }

  public String getStream() {
    return this.stream;
  }

  public String getRoutingKey(){
    return this.routingKey;
  }

  public PravegaRetention getRetention(){
    return this.retention;
  }

  public String toJson() {
    Gson gson = new Gson();
    return gson.toJson(this);
  }

  public static PravegaConfig fromJson(String json) {
    Gson gson = new Gson();
    return gson.fromJson(json, PravegaConfig.class);
  }
}
