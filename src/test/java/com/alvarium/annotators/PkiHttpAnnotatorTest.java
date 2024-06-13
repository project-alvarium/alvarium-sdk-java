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

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;

import com.alvarium.SdkInfo;
import com.alvarium.annotators.http.Ed2551RequestHandler;
import com.alvarium.annotators.http.RequestHandlerException;
import com.alvarium.contracts.Annotation;
import com.alvarium.contracts.AnnotationType;
import com.alvarium.contracts.DerivedComponent;
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

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PkiHttpAnnotatorTest {
  final AnnotatorFactory annotatorFactory = new AnnotatorFactory();
  final KeyInfo pubKey =
      new KeyInfo("./src/test/java/com/alvarium/annotators/public.key", SignType.Ed25519);
  final KeyInfo privKey =
      new KeyInfo("./src/test/java/com/alvarium/annotators/private.key", SignType.Ed25519);
  final SignatureInfo sigInfo = new SignatureInfo(pubKey, privKey);
  final byte[] data = String.format("{key: \"test\"}").getBytes();

  HttpPost getRequest(SignatureInfo sigInfo) throws RequestHandlerException {
    HttpPost request = new HttpPost(URI.create("http://example.com/foo?var1=&var2=2"));
    Date date = new Date();
    request.setHeader("Date", date.toString());
    request.setHeader("Content-Type", "application/json");
    request.setHeader("Content-Length", "18");
    String[] fields = {
      DerivedComponent.METHOD.getValue(),
      DerivedComponent.PATH.getValue(),
      DerivedComponent.AUTHORITY.getValue(),
      "Content-Type",
      "Content-Length"
    };
    Ed2551RequestHandler requestHandler = new Ed2551RequestHandler(request);
    requestHandler.addSignatureHeaders(date, fields, sigInfo);
    return request;
  }

  @Rule public ExpectedException exceptionRule = ExpectedException.none();

  @Test
  // Tests the Signature signed by the assembler
  public void testAnnotationOK() throws AnnotatorException, RequestHandlerException {
    // init logger
    final Logger logger = LogManager.getRootLogger();
    Configurator.setRootLevel(Level.DEBUG);
    HttpPost request = getRequest(sigInfo);
    try {
      request.setEntity(new StringEntity("{key: \"test\"}"));
    } catch (UnsupportedEncodingException e) {
      throw new AnnotatorException("Unsupported Character Encoding", e);
    }

    HashMap<String, Object> map = new HashMap<>();
    map.put(AnnotationType.PKIHttp.name(), request);
    final PropertyBag ctx = new ImmutablePropertyBag(map);

    final AnnotatorConfig annotatorInfo = this.getAnnotatorCfg();
    final AnnotatorConfig[] annotators = {annotatorInfo};
    final SdkInfo config =
        new SdkInfo(
            annotators,
            new HashInfo(HashType.SHA256Hash),
            sigInfo,
            null,
            LayerType.Application);
    final Annotator annotator = annotatorFactory.getAnnotator(annotatorInfo, config, logger);
    final Annotation annotation = annotator.execute(ctx, data);
    assertTrue("isSatisfied should be true", annotation.getIsSatisfied());
  }

  @Test
  public void testInvalidKeyType() throws AnnotatorException, RequestHandlerException {
    final String signatureInput =
        "\"@method\" \"@path\" \"@authority\" \"Content-Type\" "
          + "\"Content-Length\";created=1646146637;keyid=\"public.key\";alg=\"invalid\"";

    // init logger
    final Logger logger = LogManager.getRootLogger();
    Configurator.setRootLevel(Level.DEBUG);
    HttpPost request = getRequest(sigInfo);
    try {
      request.setEntity(new StringEntity("{key: \"test\"}"));
    } catch (UnsupportedEncodingException e) {
      throw new AnnotatorException("Unsupported Character Encoding", e);
    }
    request.setHeader("Signature-Input", signatureInput);

    HashMap<String, Object> map = new HashMap<>();
    map.put(AnnotationType.PKIHttp.name(), request);
    final PropertyBag ctx = new ImmutablePropertyBag(map);

    exceptionRule.expect(AnnotatorException.class);
    exceptionRule.expectMessage("Invalid key type invalid");

    final AnnotatorConfig annotatorInfo = this.getAnnotatorCfg();
    final AnnotatorConfig[] annotators = {annotatorInfo};
    final SdkInfo config =
        new SdkInfo(
            annotators,
            new HashInfo(HashType.SHA256Hash),
            sigInfo,
            null,
            LayerType.Application);
    final Annotator annotator = annotatorFactory.getAnnotator(annotatorInfo, config, logger);
    annotator.execute(ctx, data);
  }

  @Test
  public void testKeyNotFound() throws AnnotatorException, RequestHandlerException {
    final String signatureInput =
        "\"@method\" \"@path\" \"@authority\" \"Content-Type\" "
            + "\"Content-Length\";created=1646146637;keyid=\"invalid\";alg=\"ed25519\"";
    // init logger
    final Logger logger = LogManager.getRootLogger();
    Configurator.setRootLevel(Level.DEBUG);

    HttpPost request = getRequest(sigInfo);
    try {
      request.setEntity(new StringEntity("{key: \"test\"}"));
    } catch (UnsupportedEncodingException e) {
      throw new AnnotatorException("Unsupported Character Encoding", e);
    }
    request.setHeader("Signature-Input", signatureInput);

    HashMap<String, Object> map = new HashMap<>();
    map.put(AnnotationType.PKIHttp.name(), request);
    final PropertyBag ctx = new ImmutablePropertyBag(map);

    final AnnotatorConfig annotatorInfo = this.getAnnotatorCfg();
    final AnnotatorConfig[] annotators = {annotatorInfo};
    final SdkInfo config =
        new SdkInfo(
            annotators,
            new HashInfo(HashType.SHA256Hash),
            sigInfo,
            null,
            LayerType.Application);
    final Annotator annotator = annotatorFactory.getAnnotator(annotatorInfo, config, logger);
    Annotation annotation = annotator.execute(ctx, data);
    assertFalse("isSatisfied should be false", annotation.getIsSatisfied());
  }

  @Test
  public void testEmptySignature() throws AnnotatorException, RequestHandlerException {
    final String signature = "";
    // init logger
    final Logger logger = LogManager.getRootLogger();
    Configurator.setRootLevel(Level.DEBUG);

    HttpPost request = getRequest(sigInfo);
    try {
      request.setEntity(new StringEntity("{key: \"test\"}"));
    } catch (UnsupportedEncodingException e) {
      throw new AnnotatorException("Unsupported Character Encoding", e);
    }
    request.setHeader("Signature", signature);

    HashMap<String, Object> map = new HashMap<>();
    map.put(AnnotationType.PKIHttp.name(), request);
    final PropertyBag ctx = new ImmutablePropertyBag(map);

    final AnnotatorConfig annotatorInfo = this.getAnnotatorCfg();
    final AnnotatorConfig[] annotators = {annotatorInfo};
    final SdkInfo config =
        new SdkInfo(
            annotators,
            new HashInfo(HashType.SHA256Hash),
            sigInfo,
            null,
            LayerType.Application);
    final Annotator annotator = annotatorFactory.getAnnotator(annotatorInfo, config, logger);
    final Annotation annotation = annotator.execute(ctx, data);
    assertFalse("isSatisfied should be false", annotation.getIsSatisfied());
  }

  @Test
  public void testInvalidSignature() throws AnnotatorException, RequestHandlerException {
    final String signature = "invalid";

    HttpPost request = getRequest(sigInfo);
    // init logger
    final Logger logger = LogManager.getRootLogger();
    Configurator.setRootLevel(Level.DEBUG);
    try {
      request.setEntity(new StringEntity("{key: \"test\"}"));
    } catch (UnsupportedEncodingException e) {
      throw new AnnotatorException("Unsupported Character Encoding", e);
    }
    request.setHeader("Signature", signature);

    HashMap<String, Object> map = new HashMap<>();
    map.put(AnnotationType.PKIHttp.name(), request);
    final PropertyBag ctx = new ImmutablePropertyBag(map);

    final AnnotatorConfig annotatorInfo = this.getAnnotatorCfg();
    final AnnotatorConfig[] annotators = {annotatorInfo};
    final SdkInfo config =
        new SdkInfo(
            annotators,
            new HashInfo(HashType.SHA256Hash),
            sigInfo,
            null,
            LayerType.Application);
    final Annotator annotator = annotatorFactory.getAnnotator(annotatorInfo, config, logger);
    final Annotation annotation = annotator.execute(ctx, data);
    assertFalse("isSatisfied should be false", annotation.getIsSatisfied());
  }

  public AnnotatorConfig getAnnotatorCfg() {
    final Gson gson =
        new GsonBuilder()
            .registerTypeAdapter(AnnotatorConfig.class, new AnnotatorConfigConverter())
            .create();
    final String json = "{\"kind\": \"pki-http\"}";
    return gson.fromJson(json, AnnotatorConfig.class);
  }
}
