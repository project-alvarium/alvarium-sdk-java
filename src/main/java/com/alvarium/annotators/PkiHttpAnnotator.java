
/*******************************************************************************
 * Copyright 2022 Dell Inc.
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
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.time.Instant;
import java.nio.file.Path;

import com.alvarium.annotators.http.ParseResult;
import com.alvarium.annotators.http.ParseResultException;
import com.alvarium.contracts.Annotation;
import com.alvarium.contracts.AnnotationLayer;
import com.alvarium.contracts.AnnotationType;
import com.alvarium.hash.HashType;
import com.alvarium.sign.KeyInfo;
import com.alvarium.sign.SignatureInfo;
import com.alvarium.sign.SignType;
import com.alvarium.utils.PropertyBag;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.logging.log4j.Logger;

class PkiHttpAnnotator extends AbstractPkiAnnotator implements Annotator {
  private final HashType hash;
  private final SignatureInfo signature;
  private final AnnotationType kind;

  protected PkiHttpAnnotator(HashType hash, SignatureInfo signature, Logger logger) {
    super(logger);
    this.hash = hash;
    this.signature = signature;
    this.kind = AnnotationType.PKIHttp;
  }

  public Annotation execute(PropertyBag ctx, byte[] data) throws AnnotatorException {
    final String key = super.deriveHash(hash, data);
    final String tag = System.getenv(TAG_ENV_KEY) == null ? "" : System.getenv(TAG_ENV_KEY);

    HttpUriRequest request;
    try {
      request = ctx.getProperty(AnnotationType.PKIHttp.name(), HttpUriRequest.class);
    } catch (IllegalArgumentException e) {
      throw new AnnotatorException(String.format("Property %s not found", AnnotationType.PKIHttp.name()));
    }
    ParseResult parsed; 
    try {
      parsed = new ParseResult(request);
    } catch (URISyntaxException e) {
      throw new AnnotatorException("Invalid request URI", e);
    } catch (ParseResultException e) {
      throw new AnnotatorException("Error parsing the request", e);
    }
    final Signable signable = new Signable(parsed.getSeed(), parsed.getSignature());

    // Use the parsed request to obtain the key name and type we should use to
    // validate the signature
    Path path = Paths.get(signature.getPublicKey().getPath());
    Path directory = path.getParent();
    String publicKeyPath = String.join("/", directory.toString(), parsed.getKeyid());

    SignType alg;
    try {
      alg = SignType.fromString(parsed.getAlgorithm());
    } catch (EnumConstantNotPresentException e) {
      throw new AnnotatorException("Invalid key type " + parsed.getAlgorithm());
    }
    KeyInfo publicKey = new KeyInfo(publicKeyPath, alg);
    SignatureInfo sig = new SignatureInfo(publicKey, signature.getPrivateKey());

    String host = "";
    boolean isSatisfied;
    try{
      host = InetAddress.getLocalHost().getHostName();

      isSatisfied = verifySignature(sig.getPublicKey(), signable);
    } catch (UnknownHostException | AnnotatorException e) {
      isSatisfied = false;
      this.logger.error("Error during PkiHttpAnnotator execution: ",e);
    }
 
    final Annotation annotation = new Annotation(
        key,
        hash,
        host,
        tag,
        AnnotationLayer.PKIHttp,
        kind,
        null,
        isSatisfied,
        Instant.now());

    final String annotationSignature = super.signAnnotation(sig.getPrivateKey(), annotation);
    annotation.setSignature(annotationSignature);
    return annotation;
  }
}
