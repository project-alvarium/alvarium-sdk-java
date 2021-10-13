
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

import java.net.URI;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import com.alvarium.PublishWrapper;

import io.pravega.client.ClientConfig;
import io.pravega.client.EventStreamClientFactory;
import io.pravega.client.admin.StreamManager;
import io.pravega.client.stream.EventStreamWriter;
import io.pravega.client.stream.EventWriterConfig;
import io.pravega.client.stream.RetentionPolicy;
import io.pravega.client.stream.ScalingPolicy;
import io.pravega.client.stream.StreamConfiguration;
import io.pravega.client.stream.impl.UTF8StringSerializer;

/**
 * A unit responsible for providing a pravega stream writer through the StreamProvider interface. 
 */
class PravegaStreamProvider implements StreamProvider {
  private final PravegaConfig config;
  private final StreamConfiguration streamConfig;
  private EventStreamWriter<String> streamWriter;

  public PravegaStreamProvider(PravegaConfig config) {
    this.config = config;
    // build the depedencies required for the pravega stream configuration
    final PravegaRetention retention = config.getRetention();
    final ScalingPolicy scalingPolicy = ScalingPolicy.fixed(config.getScalingPolicy());
    final RetentionPolicy retentionPolicy = RetentionPolicy.builder()
        .retentionType(retention.getType())
        .retentionParam(retention.getMin())
        .retentionMax(retention.getMax())
        .build();
    this.streamConfig = StreamConfiguration.builder()
        .scalingPolicy(scalingPolicy)
        .retentionPolicy(retentionPolicy)
        .build();
  }

  public void connect() throws StreamException {
    this.reconnect();
  }
  
  private void reconnect() throws StreamException {
    if(this.streamWriter != null){
      return;
    }

    // methods used inside this try/catch block do not specify the type of exception thrown.
    // hence, a general exception is caught and a StreamException is thrown.
    try {
      final URI controllerUri = URI.create(this.config.getProvider().uri());

      // make sure that the selected scope & stream are created.
      // this routine should check if the scope/stream exists before creating new ones. 
      final StreamManager streamManager = StreamManager.create(controllerUri); 
      streamManager.createScope(config.getScope());
      streamManager.createStream(config.getScope(), config.getStream(), this.streamConfig);

      // Init the pravega stream writer using the specified scope/stream.
      EventStreamClientFactory clientFactory = EventStreamClientFactory.withScope(config.getScope(),
          ClientConfig.builder().controllerURI(controllerUri).build());
      this.streamWriter = clientFactory.createEventWriter(config.getStream(),
          new UTF8StringSerializer(), EventWriterConfig.builder().build());
    } catch (Exception e) {
      throw new StreamException("cannot connect to stream.", e);
    }
  }

  public void close() throws StreamException {
    this.streamWriter.close();
  }

  public void publish(PublishWrapper wrapper) throws StreamException {
    this.reconnect();

    try {
      // writes the data to the stream, and waits on the completable future until it fully completes 
      this.streamWriter.writeEvent(this.config.getRoutingKey(),wrapper.toJson()).get();
    } catch (CancellationException e) {
      throw new StreamException("publishing process was cancelled.", e);
    } catch (ExecutionException e){
      throw new StreamException("publishing process could not resolve.", e);
    } catch (InterruptedException e){
      throw new StreamException("publishing process was interrupted.", e);
    }
  }
}
