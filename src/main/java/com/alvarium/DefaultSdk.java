
/*******************************************************************************
 * Copyright 2023 Dell Inc.
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

import com.alvarium.annotators.Annotator;
import com.alvarium.annotators.AnnotatorConfig;
import com.alvarium.annotators.AnnotatorException;
import com.alvarium.annotators.AnnotatorFactory;
import com.alvarium.contracts.Annotation;
import com.alvarium.contracts.AnnotationList;
import com.alvarium.contracts.AnnotationType;
import com.alvarium.hash.HashProviderFactory;
import com.alvarium.hash.HashTypeException;
import com.alvarium.streams.StreamException;
import com.alvarium.streams.StreamProvider;
import com.alvarium.streams.StreamProviderFactory;
import com.alvarium.utils.ImmutablePropertyBag;
import com.alvarium.utils.PropertyBag;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DefaultSdk implements Sdk {
  private final Annotator[] annotators;
  private final SdkInfo config;
  private final StreamProvider stream;
  private final Logger logger;

  public DefaultSdk(Annotator[] annotators, SdkInfo config, Logger logger) throws StreamException {
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
      StreamException, HashTypeException {
    final List<Annotation> annotations = this.createAnnotations(properties, data);
    this.publishAnnotations(SdkAction.CREATE, annotations);
    this.logger.debug("data annotated and published successfully.");
  }

  public void create(byte[] data) throws AnnotatorException, StreamException, HashTypeException {
    final PropertyBag properties = new ImmutablePropertyBag(new HashMap<>());
    this.create(properties, data);
  }

  public void mutate(PropertyBag properties, byte[] oldData, byte[] newData) throws
      AnnotatorException, StreamException, HashTypeException {
    final List<Annotation> annotations = new ArrayList<Annotation>();

    // source annotate the old data
    final AnnotatorFactory annotatorFactory = new AnnotatorFactory();
    final Annotator sourceAnnotator = annotatorFactory.getAnnotator(
        new AnnotatorConfig(AnnotationType.SOURCE),
        this.config,
        this.logger
    );

    String key;
    var hasher = new HashProviderFactory().getProvider(config.getHash().getType());
    key = hasher.derive(oldData);

    final Annotation sourceAnnotation = sourceAnnotator.execute(properties, oldData, key);
    annotations.add(sourceAnnotation);

    // Add annotations for new data
    for (Annotation annotation : this.createAnnotations(properties, newData)) {
      // TLS is ignored in mutate to prevent needless penalization
      // See https://github.com/project-alvarium/alvarium-sdk-go/issues/19
      if (annotation.getKind() != AnnotationType.TLS) {
        annotations.add(annotation);
      }
    }

    // publish to the stream provider
    this.publishAnnotations(SdkAction.MUTATE, annotations);
    this.logger.debug("data annotated and published successfully.");
  }

  public void mutate(byte[] oldData, byte[] newData) throws AnnotatorException, StreamException, HashTypeException {
    final PropertyBag properties = new ImmutablePropertyBag(new HashMap<>());
    this.mutate(properties, oldData, newData);
  }

  public void transit(PropertyBag properties, byte[] data) throws AnnotatorException,
      StreamException, HashTypeException {
    final List<Annotation> annotations = this.createAnnotations(properties, data);
    this.publishAnnotations(SdkAction.TRANSIT, annotations);
    this.logger.debug("data annotated and published successfully.");
  }

  public void transit(byte[] data) throws AnnotatorException, StreamException, HashTypeException {
    final PropertyBag properties = new ImmutablePropertyBag(new HashMap<String, Object>());
    this.transit(properties, data);
  }

  public void publish(PropertyBag properties, byte[] data) throws AnnotatorException,
      StreamException, HashTypeException {
    final List<Annotation> annotations = this.createAnnotations(properties, data);
    this.publishAnnotations(SdkAction.PUBLISH, annotations);
    this.logger.debug("data annotated and published successfully.");
  }

  public void publish(byte[] data) throws AnnotatorException, StreamException, HashTypeException {
    final PropertyBag properties = new ImmutablePropertyBag(new HashMap<>());
    this.publish(properties, data);
  }

  public void close() throws StreamException {
    this.stream.close();
    this.logger.debug("stream provider connection terminated successfully.");
  }

  /**
   * Executes all the specified annotators and returns a list of all the created annotations
   *
   * @param properties
   * @param data
   * @return
   * @throws AnnotatorException
   */
  private List<Annotation> createAnnotations(PropertyBag properties, byte[] data)
      throws AnnotatorException, HashTypeException {
    final List<Annotation> annotations = new ArrayList<>();

    String key;
    var hasher = new HashProviderFactory().getProvider(config.getHash().getType());
    key = hasher.derive(data);

    // Annotate incoming data
    for (Annotator annotator : this.annotators) {
      final Annotation annotation = annotator.execute(properties, data, key);
      annotations.add(annotation);
    }

    return annotations;
  }

  /**
   * Wraps the annotation list with a publish wrapper that specifies the SDK action and the
   * content type
   *
   * @param action
   * @param annotations
   * @throws StreamException
   */
  private void publishAnnotations(SdkAction action, List<Annotation> annotations)
      throws StreamException {
    final AnnotationList annotationList = new AnnotationList(annotations);

    // publish list of annotations to the StreamProvider
    final PublishWrapper wrapper = new PublishWrapper(
        action,
        annotationList.getClass().getName(),
        annotationList
    );
    this.stream.publish(wrapper);
  }
}
