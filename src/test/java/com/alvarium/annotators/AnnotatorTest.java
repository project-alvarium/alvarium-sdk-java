
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
package com.alvarium.annotators;

import java.util.HashMap;

import com.alvarium.SdkInfo;
import com.alvarium.contracts.Annotation;
import com.alvarium.hash.HashInfo;
import com.alvarium.hash.HashType;
import com.alvarium.serializers.AnnotatorConfigConverter;
import com.alvarium.sign.KeyInfo;
import com.alvarium.sign.SignType;
import com.alvarium.sign.SignatureInfo;
import com.alvarium.utils.ImmutablePropertyBag;
import com.alvarium.utils.PropertyBag;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.Test;

public class AnnotatorTest {


  @Test
  public void mockAnnotatorShouldReturnSatisfiedAnnotation() throws AnnotatorException {
    final KeyInfo keyInfo = new KeyInfo("path", SignType.none);
    final SignatureInfo signature = new SignatureInfo(keyInfo, keyInfo);
    final HashInfo hash = new HashInfo(HashType.NoHash);
    
    final Gson gson = new GsonBuilder()
      .registerTypeAdapter(AnnotatorConfig.class, new AnnotatorConfigConverter())
      .create();
    
    final String satisfiedMockConfig = "{\"kind\": \"mock\",\n\"shouldSatisfy\":true}";
    final String unsatisfiedMockConfig = "{\"kind\": \"mock\",\n\"shouldSatisfy\":false}";
    
    
    final String badConfig1 = "{\"kind\": \"invalid\",\n\"shouldSatisfy\":true}";
    final String badConfig2 = "{\"invalid\": \"mock\",\n\"shouldSatisfy\":true}";

    
    final AnnotatorConfig satisfiedAnnotatorInfo = gson.fromJson(satisfiedMockConfig, AnnotatorConfig.class);
    final AnnotatorConfig unsatisfiedAnnotatorInfo = gson.fromJson(unsatisfiedMockConfig, AnnotatorConfig.class);


    try {
      gson.fromJson(badConfig1, AnnotatorConfig.class);
      assert false : "Expected IllegalArgumentException due to invalid kind";
    } catch (IllegalArgumentException e) {
      assert true;
    }

    try {
      gson.fromJson(badConfig2, AnnotatorConfig.class);
      assert false : "Expected IllegalArgumentException due to missing kind property";
    } catch (IllegalArgumentException e) {
      assert true;
    }
    
    final AnnotatorConfig[] annotators = {satisfiedAnnotatorInfo, unsatisfiedAnnotatorInfo};
    final SdkInfo config = new SdkInfo(annotators, hash, signature, null);

    final Logger logger = LogManager.getRootLogger();
    Configurator.setRootLevel(Level.DEBUG);

    final AnnotatorFactory factory = new AnnotatorFactory();
    final Annotator satisfiedAnnotator = factory.getAnnotator(satisfiedAnnotatorInfo, config, logger);
    final Annotator unsatisfiedAnnotator = factory.getAnnotator(unsatisfiedAnnotatorInfo, config, logger);

    final byte[] data = "test data".getBytes();
    final PropertyBag ctx = new ImmutablePropertyBag(new HashMap<>());

    final Annotation satisfiedAnnotation = satisfiedAnnotator.execute(ctx, data);
    final Annotation unsatisfiedAnnotation = unsatisfiedAnnotator.execute(ctx, data);

    assert satisfiedAnnotation.getIsSatisfied();
    assert !unsatisfiedAnnotation.getIsSatisfied();
  }  
}
