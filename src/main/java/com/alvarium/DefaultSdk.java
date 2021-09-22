package com.alvarium;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.alvarium.annotators.Annotator;
import com.alvarium.annotators.AnnotatorException;
import com.alvarium.annotators.AnnotatorFactory;
import com.alvarium.contracts.Annotation;
import com.alvarium.contracts.AnnotationType;
import com.alvarium.streams.StreamException;
import com.alvarium.streams.StreamProvider;
import com.alvarium.streams.StreamProviderFactory;
import com.alvarium.utils.ImmutablePropertyBag;
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

  public void create(PropertyBag properties, byte[] data) throws AnnotatorException, 
      StreamException {
    final List<Annotation> annotations = new ArrayList<Annotation>();
    final String contentType = "AnnotationList";

    // annotate incoming data with the given list of annotators
    for (Annotator annotator: this.annotators) {
      final Annotation annotation = annotator.execute(properties, data);
      annotations.add(annotation);
    }

    final PublishWrapper wrapper = new PublishWrapper(SdkAction.CREATE, contentType, annotations);
    this.stream.publish(wrapper);
    this.logger.debug("data annotated and published successfully.");
  }
  
  public void create(byte[] data) throws AnnotatorException, StreamException {
    final PropertyBag properties = new ImmutablePropertyBag(new HashMap<String, Object>());
    this.create(properties, data);
  }

  public void mutate(PropertyBag properties, byte[] oldData, byte[] newData) throws 
  AnnotatorException, StreamException {
    // source annotate the old data
    final AnnotatorFactory annotatorFactory = new AnnotatorFactory();
    final Annotator sourceAnnotator = annotatorFactory.getAnnotator(AnnotationType.SOURCE,
        this.config.getHash().getType(), this.config.getSignature());
    final Annotation sourceAnnotation = sourceAnnotator.execute(properties, oldData);

    final List<Annotation> annotations = new ArrayList<Annotation>();
    annotations.add(sourceAnnotation);
    final String contentType = "AnnotationList";

    for (Annotator annotator: this.annotators) {
      final Annotation annotation = annotator.execute(properties, newData);
      annotations.add(annotation);
    }

    final PublishWrapper wrapper = new PublishWrapper(SdkAction.MUTATE, contentType, annotations);
    this.stream.publish(wrapper);
    this.logger.debug("data annotated and published successfully.");
  }

  public void mutate(byte[] oldData, byte[] newData) throws AnnotatorException, StreamException {
    final PropertyBag properties = new ImmutablePropertyBag(new HashMap<String, Object>());
    this.mutate(properties, oldData, newData);
  }

  public void transit(PropertyBag properties, byte[] data) throws AnnotatorException,
      StreamException {
    final List<Annotation> annotations = new ArrayList<Annotation>();
    final String contentType = "AnnotationList";

    // Annotate incoming data
    for (Annotator annotator: this.annotators) {
      final Annotation annotation = annotator.execute(properties, data);
      annotations.add(annotation);
    }

    // publish list of annotations to the StreamProvider
    final PublishWrapper wrapper = new PublishWrapper(SdkAction.TRANSIT, contentType, annotations);
    this.stream.publish(wrapper);
    this.logger.debug("data annotated and published successfully.");
  }

  public void transit(byte[] data) throws AnnotatorException, StreamException {
    final PropertyBag properties = new ImmutablePropertyBag(new HashMap<String, Object>());
    this.transit(properties, data);
  }

  public void close() throws StreamException {
    this.stream.close();
    this.logger.debug("stream provider connection terminated successfully.");
  }
}
