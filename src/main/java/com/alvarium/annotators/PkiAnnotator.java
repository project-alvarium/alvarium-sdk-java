
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

import com.alvarium.contracts.Annotation;
import com.alvarium.contracts.AnnotationType;
import com.alvarium.hash.HashType;
import com.alvarium.sign.SignatureInfo;
import com.alvarium.utils.PropertyBag;

class PkiAnnotator extends AbstractPkiAnnotator implements Annotator {
  private final HashType hash;
  private final SignatureInfo signature;
  private final AnnotationType kind;

  protected PkiAnnotator(HashType hash, SignatureInfo signature) {
    this.hash = hash;
    this.signature = signature;
    this.kind = AnnotationType.PKI;
  }

  public Annotation execute(PropertyBag ctx, byte[] data) throws AnnotatorException {
    final String key = super.deriveHash(hash, data);
    
    String host;
    try {
      host = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      throw new AnnotatorException("Cannot get host name", e);
    }  

    final Signable signable = Signable.fromJson(new String(data));

    Boolean isSatisfied = verifySignature(signature.getPublicKey(), signable);
    
    final Annotation annotation = new Annotation(
        key, 
        hash, 
        host, 
        kind, 
        null, 
        isSatisfied, 
        Instant.now());

    final String annotationSignature = super.signAnnotation(signature.getPrivateKey(), annotation);
    annotation.setSignature(annotationSignature);
    return annotation;

  }
  
}
