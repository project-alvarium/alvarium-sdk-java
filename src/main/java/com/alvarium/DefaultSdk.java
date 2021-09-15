package com.alvarium;

import com.alvarium.annotators.Annotator;
import com.alvarium.streams.StreamException;
import com.alvarium.streams.StreamProvider;
import com.alvarium.streams.StreamProviderFactory;
import com.alvarium.utils.PropertyBag;

import org.apache.logging.log4j.Logger;

public class DefaultSdk implements Sdk {
  private final Annotator[] annotators;
  private final SdkInfo config;
  private final StreamProvider stream;
  private final Logger logger;

  DefaultSdk(Annotator[] annotators, SdkInfo config, Logger logger) throws StreamException {
    this.annotators = annotators;
    this.config = config;
    this.logger = logger;

    // init stream
    final StreamProviderFactory streamFactory = new StreamProviderFactory();
    this.stream = streamFactory.getProvider(this.config.getStream());
    this.stream.connect();
    this.logger.debug("stream provider connected successfully.");
  }

  public void create(PropertyBag properties, byte[] data) {
  }

  public void mutate(PropertyBag properties, byte[] oldData, byte[] newData) {
  }

  public void transit(PropertyBag properties, byte[] data) {
  }

  public void close() throws StreamException {
    this.stream.close();
  }
}
