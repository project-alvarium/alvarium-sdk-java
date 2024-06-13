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
package com.alvarium.annotators;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

public class PkiAnnotatorTest {

  @Test
  public void executeShouldGetSatisfiedAnnotation() throws AnnotatorException {
    final AnnotatorFactory annotatorFactory = new AnnotatorFactory();
    final KeyInfo pubKey =
        new KeyInfo("./src/test/java/com/alvarium/annotators/public.key", SignType.Ed25519);
    final KeyInfo privKey =
        new KeyInfo(
            "./src/test/java/com/alvarium/annotators/private.key", SignType.Ed25519);
    final SignatureInfo sigInfo = new SignatureInfo(pubKey, privKey);

    // init logger
    final Logger logger = LogManager.getRootLogger();
    Configurator.setRootLevel(Level.DEBUG);

    final PropertyBag ctx = new ImmutablePropertyBag(new HashMap<String, Object>());

    final String signature =
        "B9E41596541933DB7144CFBF72105E4E53F9493729CA66331A658B1B18AC6DF5DA991"
            + "AD9720FD46A664918DFC745DE2F4F1F8C29FF71209B2DA79DFD1A34F50C";

    final byte[] data =
        String.format("{seed: \"helloo\", signature: \"%s\"}", signature).getBytes();

    final AnnotatorConfig pkiCfg = this.getAnnotatorCfg();
    final AnnotatorConfig[] annotators = {pkiCfg};
    final SdkInfo config =
        new SdkInfo(
            annotators,
            new HashInfo(HashType.SHA256Hash),
            sigInfo,
            null,
            LayerType.Application);
    final Annotator annotator = annotatorFactory.getAnnotator(pkiCfg, config, logger);
    final Annotation annotation = annotator.execute(ctx, data);
    assertTrue("isSatisfied should be true", annotation.getIsSatisfied());
  }

  @Test
  public void executeShouldGetUnsatisfiedAnnotation() throws AnnotatorException {
    final AnnotatorFactory annotatorFactory = new AnnotatorFactory();
    final KeyInfo pubKey =
        new KeyInfo("./src/test/java/com/alvarium/annotators/public.key", SignType.Ed25519);
    final KeyInfo privKey =
        new KeyInfo(
            "./src/test/java/com/alvarium/annotators/private.key", SignType.Ed25519);
    final SignatureInfo sigInfo = new SignatureInfo(pubKey, privKey);

    // init logger
    final Logger logger = LogManager.getRootLogger();
    Configurator.setRootLevel(Level.DEBUG);

    final PropertyBag ctx = new ImmutablePropertyBag(new HashMap<String, Object>());

    final String signature =
        "A9E41596541933DB7144CFBF72105E4E53F9493729CA66331A658B1B18AC6DF5DA991"
            + "AD9720FD46A664918DFC745DE2F4F1F8C29FF71209B2DA79DFD1A34F50C";

    final byte[] data =
        String.format("{seed: \"helloo\", signature: \"%s\"}", signature).getBytes();

    final AnnotatorConfig pkiCfg = this.getAnnotatorCfg();
    final AnnotatorConfig[] annotators = {pkiCfg};
    final SdkInfo config =
        new SdkInfo(
            annotators,
            new HashInfo(HashType.SHA256Hash),
            sigInfo,
            null,
            LayerType.Application);
    final Annotator annotator = annotatorFactory.getAnnotator(pkiCfg, config, logger);

    final Annotation annotation = annotator.execute(ctx, data);
    assertFalse("isSatisfied should be false", annotation.getIsSatisfied());
  }

  public AnnotatorConfig getAnnotatorCfg() {
    final Gson gson =
        new GsonBuilder()
            .registerTypeAdapter(AnnotatorConfig.class, new AnnotatorConfigConverter())
            .create();
    final String json = "{\"kind\": \"pki\"}";
    return gson.fromJson(json, AnnotatorConfig.class);
  }
}
