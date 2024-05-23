
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
package com.alvarium.annotators;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.alvarium.contracts.Annotation;
import com.alvarium.contracts.AnnotationType;
import com.alvarium.contracts.LayerType;
import com.alvarium.hash.HashType;
import com.alvarium.sign.SignatureInfo;
import com.alvarium.tag.TagManager;
import com.alvarium.utils.PropertyBag;

class PkiAnnotator extends AbstractPkiAnnotator implements Annotator {
  private final HashType hash;
  private final SignatureInfo signature;
  private final AnnotationType kind;
  private final LayerType layer;
  private final TagManager tagManager;

  protected PkiAnnotator(HashType hash, SignatureInfo signature, Logger logger, LayerType layer) {
    super(logger);
    this.hash = hash;
    this.signature = signature;
    this.kind = AnnotationType.PKI;
    this.layer = layer;
    this.tagManager = new TagManager(layer);
  }

  public Annotation execute(PropertyBag ctx, byte[] data) throws AnnotatorException {
    final String key = super.deriveHash(hash, data);

    final Signable signable = Signable.fromJson(new String(data));

    String host = "";
    boolean isSatisfied;
    try {
      host = InetAddress.getLocalHost().getHostName();

      isSatisfied = verifySignature(signature.getPublicKey(), signable);
    } catch (UnknownHostException | AnnotatorException e) {
      isSatisfied = false;
      this.logger.error("Error during PkiAnnotator execution: ", e);
    }

    final Annotation annotation = new Annotation(
        key,
        hash,
        host,
        layer,
        kind,
        null,
        isSatisfied,
        Instant.now());

    if (ctx.hasProperty("tagWriterOverrides")) {
      annotation.setTag(tagManager.getTagValue(ctx.getProperty("tagWriterOverrides", Map.class)));
    } else {
      annotation.setTag(tagManager.getTagValue());
    }

    final String annotationSignature = super.signAnnotation(signature.getPrivateKey(), annotation);
    annotation.setSignature(annotationSignature);
    return annotation;

  }

}
