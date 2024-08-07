
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

/**
 * A unit used to provide lineage from one version of data to another as a
 * result of
 * change or transformation
 */
class SourceAnnotator extends AbstractAnnotator implements Annotator {
  private final HashType hash;
  private final AnnotationType kind;
  private final SignatureInfo signatureInfo;
  private final LayerType layer;
  private final TagManager tagManager;

  protected SourceAnnotator(HashType hash, SignatureInfo signatureInfo, Logger logger, LayerType layer) {
    super(logger);
    this.hash = hash;
    this.kind = AnnotationType.SOURCE;
    this.signatureInfo = signatureInfo;
    this.layer = layer;
    this.tagManager = new TagManager(layer);
  }

  public Annotation execute(PropertyBag ctx, byte[] data) throws AnnotatorException {
    // hash incoming data
    final String key = super.deriveHash(this.hash, data);

    // get hostname if available
    String host = "";
    boolean isSatisfied;
    try {
      host = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      isSatisfied = false;
      this.logger.error("Error during SourceAnnotator execution: ", e);
    }

    isSatisfied = true;

    // create an annotation without signature
    final Annotation annotation = new Annotation(key, this.hash, host, layer, this.kind, null, isSatisfied,
        Instant.now());

    if (ctx.hasProperty("tagWriterOverrides")) {
      annotation.setTag(tagManager.getTagValue(ctx.getProperty("tagWriterOverrides", Map.class)));
    } else {
      annotation.setTag(tagManager.getTagValue());
    }

    final String signature = super.signAnnotation(signatureInfo.getPrivateKey(), annotation);
    annotation.setSignature(signature);
    return annotation;
  }
}
