
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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.Map;

import com.alvarium.contracts.Annotation;
import com.alvarium.contracts.AnnotationType;
import com.alvarium.contracts.LayerType;
import com.alvarium.hash.HashProviderFactory;
import com.alvarium.hash.HashType;
import com.alvarium.hash.HashTypeException;
import com.alvarium.sign.SignatureInfo;
import com.alvarium.tag.TagManager;
import com.alvarium.utils.PropertyBag;

/**
 * a dummy annotator to be used in unit tests
 */
class MockAnnotator implements Annotator {
  private final MockAnnotatorConfig cfg;
  private final HashType hash;
  private final AnnotationType kind;
  private final SignatureInfo signature;
  private final LayerType layer;
  private final TagManager tagManager;

  protected MockAnnotator(MockAnnotatorConfig cfg, HashType hash, SignatureInfo signature, LayerType layer) {
    this.cfg = cfg;
    this.hash = hash;
    this.kind = AnnotationType.MOCK;
    this.signature = signature;
    this.layer = layer;
    this.tagManager = new TagManager(layer);
  }

  public Annotation execute(PropertyBag ctx, byte[] data) throws AnnotatorException {
    final HashProviderFactory hashFactory = new HashProviderFactory();
    try {
      final String key = hashFactory.getProvider(hash).derive(data);
      final String host = InetAddress.getLocalHost().getHostName();
      final String sig = signature.getPublicKey().getType().toString();

      final Annotation annotation = new Annotation(key, hash, host, layer, kind, sig, cfg.getShouldSatisfy(),
          Instant.now());
      if (ctx.hasProperty("tagWriterOverrides")) {
        annotation.setTag(tagManager.getTagValue(ctx.getProperty("tagWriterOverrides", Map.class)));
      } else {
        annotation.setTag(tagManager.getTagValue());
      }
      return annotation;
    } catch (HashTypeException e) {
      throw new AnnotatorException("failed to hash data", e);
    } catch (UnknownHostException e) {
      throw new AnnotatorException("Could not get hostname", e);
    }
  }
}
