
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
package com.alvarium;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import com.alvarium.utils.PropertyBag;
import com.alvarium.annotators.Annotator;
import com.alvarium.annotators.AnnotatorException;
import com.alvarium.annotators.AnnotatorFactory;
import com.alvarium.streams.StreamException;
import com.alvarium.utils.ImmutablePropertyBag;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.Test;

class MockSdk implements Sdk {
  public void create(PropertyBag properties, byte[] data) {}
  public void create(byte[] data) {
    final PropertyBag properties = new ImmutablePropertyBag(new HashMap<String, Object>());
    this.create(properties, data);
  }
  public void mutate(PropertyBag properties, byte[] oldData, byte[] newData) {}
  public void mutate(byte[] oldData, byte[] newData) {
    final PropertyBag properties = new ImmutablePropertyBag(new HashMap<String, Object>());
    this.mutate(properties, oldData, newData);
  }
  public void transit(PropertyBag properties, byte[] data) {}
  public void transit(byte[] data) {
    final PropertyBag properties = new ImmutablePropertyBag(new HashMap<String, Object>());
    this.transit(properties, data);
  }
  public void close() {
    System.out.println("Connections closed");
  } 
}
public class SdkTest {
  private final String testJson;

  public SdkTest() throws IOException {
    String path = "./src/test/java/com/alvarium/mock-info.json";
    this.testJson = Files.readString(Paths.get(path), StandardCharsets.US_ASCII);
  }

  @Test
  public void instantiateSdkShouldNotThrow() throws AnnotatorException, StreamException {
    final SdkInfo sdkInfo = SdkInfo.fromJson(this.testJson);

    // init annotators
    final Annotator[] annotators = new Annotator[sdkInfo.getAnnotators().length]; 
    final AnnotatorFactory annotatorFactory = new AnnotatorFactory();

    for (int i = 0; i < annotators.length; i++) {
      annotators[i] = annotatorFactory.getAnnotator(sdkInfo.getAnnotators()[i], sdkInfo);
    }

    // init logger
    final Logger logger = LogManager.getRootLogger();
    Configurator.setRootLevel(Level.DEBUG);

    final Sdk sdk = new DefaultSdk(annotators, sdkInfo, logger);
    sdk.close();
  }

  @Test
  public void createShouldReturnSameData() throws AnnotatorException, StreamException {
    final Sdk sdk = new MockSdk();
    byte[] oldData = {0xA, 0x1};
    byte[] newData = {0x1, 0xA};
    sdk.create(oldData);
    sdk.mutate(oldData, newData);
    sdk.transit(oldData);
  }

  @Test
  public void defaultSdkShouldCreateAnnotations() throws AnnotatorException, StreamException {
    final SdkInfo sdkInfo = SdkInfo.fromJson(this.testJson);

    // init annotators
    final Annotator[] annotators = new Annotator[sdkInfo.getAnnotators().length];
    final AnnotatorFactory annotatorFactory = new AnnotatorFactory();

    for (int i = 0; i < annotators.length; i++) {
      annotators[i] = annotatorFactory.getAnnotator(sdkInfo.getAnnotators()[i], sdkInfo); 
    }

    // init logger and sdk
    final Logger logger = LogManager.getRootLogger();
    Configurator.setRootLevel(Level.DEBUG);
    final Sdk sdk = new DefaultSdk(annotators, sdkInfo, logger);

    final byte[] data = "test data".getBytes();

    sdk.create(data);
    sdk.close();
  }

  @Test
  public void defaultSdkShouldCreateTransitionAnnotations() throws AnnotatorException,
      StreamException {
    final SdkInfo sdkInfo = SdkInfo.fromJson(this.testJson);

    // init annotators
    final Annotator[] annotators = new Annotator[sdkInfo.getAnnotators().length];
    final AnnotatorFactory annotatorFactory = new AnnotatorFactory();

    for (int i = 0; i < annotators.length; i++) {
      annotators[i] = annotatorFactory.getAnnotator(sdkInfo.getAnnotators()[i], sdkInfo); 
    }

    // init logger and sdk
    final Logger logger = LogManager.getRootLogger();
    Configurator.setRootLevel(Level.DEBUG);
    final Sdk sdk = new DefaultSdk(annotators, sdkInfo, logger);

    final byte[] data = "test data".getBytes();

    sdk.transit(data);
    sdk.close();
  }

  @Test
  public void defaultSdkShouldMutateData() throws AnnotatorException, StreamException {
    final SdkInfo sdkInfo = SdkInfo.fromJson(this.testJson);

    // init annotators
    final Annotator[] annotators = new Annotator[sdkInfo.getAnnotators().length];
    final AnnotatorFactory annotatorFactory = new AnnotatorFactory();

    for (int i = 0; i < annotators.length; i++) {
      annotators[i] = annotatorFactory.getAnnotator(sdkInfo.getAnnotators()[i], sdkInfo); 
    }

    // init logger and sdk
    final Logger logger = LogManager.getRootLogger();
    Configurator.setRootLevel(Level.DEBUG);
    final Sdk sdk = new DefaultSdk(annotators, sdkInfo, logger);


    final byte[] oldData = "old data".getBytes();
    final byte[] newData = "new data".getBytes();

    sdk.mutate(oldData, newData);
    sdk.close();
  }
}
