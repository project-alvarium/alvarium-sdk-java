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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZonedDateTime;

import com.alvarium.annotators.sbom.SbomException;
import org.apache.logging.log4j.Logger;

import com.alvarium.annotators.sbom.SbomAnnotatorConfig;
import com.alvarium.annotators.sbom.SbomProvider;
import com.alvarium.annotators.sbom.SbomProviderFactory;
import com.alvarium.contracts.Annotation;
import com.alvarium.contracts.AnnotationType;
import com.alvarium.contracts.LayerType;
import com.alvarium.hash.HashType;
import com.alvarium.sign.SignatureInfo;
import com.alvarium.utils.PropertyBag;

public class SbomAnnotator extends AbstractAnnotator implements Annotator {
  final SbomAnnotatorConfig cfg;

  final HashType hash;
  final SignatureInfo signature;
  final AnnotationType kind;
  final LayerType layer;

  protected SbomAnnotator(SbomAnnotatorConfig cfg, HashType hash, SignatureInfo signature, Logger logger, LayerType layer) {
    super(logger);
    this.cfg = cfg;
    this.hash = hash;
    this.signature = signature;
    this.kind = AnnotationType.SBOM;
    this.layer = layer;
  }

  @Override
  public Annotation execute(PropertyBag ctx, byte[] data, String key) throws AnnotatorException {
    String host = "";
    try {
      host = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      this.logger.error("Error during SbomAnnotator execution: ", e);
    }

    boolean isSatisfied = false;
    try {
      final SbomProvider sbom = new SbomProviderFactory().getProvider(this.cfg, this.logger);
      final String filePath = ctx.getProperty(AnnotationType.SBOM.name(), String.class);
      boolean isValid = sbom.validate(filePath);
      boolean exists = sbom.exists(filePath);
      boolean matchesBuild = sbom.matchesBuild(filePath, ".");
      isSatisfied = isValid && exists && matchesBuild;
    } catch (SbomException e) {
      this.logger.error("Error during SbomAnnotator execution: ", e);
    } catch (Exception e) {
      this.logger.error("Error during SbomAnnotator execution: ", e);
    }

    final Annotation annotation = new Annotation(
        key,
        hash,
        host,
        layer,
        kind,
        null,
        isSatisfied,
        ZonedDateTime.now()
    );

    final String annotationSignature = super.signAnnotation(
        this.signature.getPrivateKey(),
        annotation
    );

    annotation.setSignature(annotationSignature);
    return annotation;
  }
}
