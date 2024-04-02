
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

import java.io.IOException;
import java.util.HashMap;

import com.alvarium.SdkInfo;
import com.alvarium.contracts.Annotation;
import com.alvarium.contracts.LayerType;
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

public class SourceAnnotatorTest {
  @Test
  public void executeShouldReturnAnnotation() throws AnnotatorException, IOException {
    // construct annotator
    final AnnotatorFactory annotatorFactory = new AnnotatorFactory();
    final KeyInfo pubKey = new KeyInfo("./src/test/java/com/alvarium/annotators/public.key", 
        SignType.Ed25519);
    final KeyInfo privKey = new KeyInfo("./src/test/java/com/alvarium/annotators/private.key",
        SignType.Ed25519);
    final SignatureInfo sigInfo = new SignatureInfo(pubKey, privKey);

    final Gson gson = new GsonBuilder()
      .registerTypeAdapter(AnnotatorConfig.class, new AnnotatorConfigConverter())
      .create();
    final String json = "{\"kind\": \"src\"}";
    final AnnotatorConfig annotatorInfo = gson.fromJson(
                json, 
                AnnotatorConfig.class
    ); 
    System.out.println("hello " + annotatorInfo.getKind());
    final AnnotatorConfig[] annotators = {annotatorInfo};  
    final SdkInfo config = new SdkInfo(annotators, new HashInfo(HashType.SHA256Hash), sigInfo, null, LayerType.Application);

            // init logger
    final Logger logger = LogManager.getRootLogger();
    Configurator.setRootLevel(Level.DEBUG);

    final Annotator annotator = annotatorFactory.getAnnotator(annotatorInfo, config, logger);

    // dummy data and empty prop bag
    final byte[] data = "test data".getBytes();
    final PropertyBag ctx = new ImmutablePropertyBag(new HashMap<String, Object>());

    final Annotation annotation = annotator.execute(ctx, data);
    System.out.println(annotation.toJson());
  }

}
