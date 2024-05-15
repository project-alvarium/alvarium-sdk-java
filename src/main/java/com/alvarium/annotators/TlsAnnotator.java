
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
import javax.net.ssl.SSLSocket;

import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.time.ZonedDateTime;

import com.alvarium.contracts.Annotation;
import com.alvarium.contracts.AnnotationType;
import com.alvarium.contracts.LayerType;
import com.alvarium.hash.HashType;
import com.alvarium.sign.SignatureInfo;
import com.alvarium.utils.PropertyBag;

class TlsAnnotator extends AbstractAnnotator implements Annotator {
  private final HashType hash;
  private final AnnotationType kind;
  private final SignatureInfo signatureInfo;
  private final LayerType layer;
  
  protected TlsAnnotator(HashType hash, SignatureInfo signatureInfo, Logger logger, LayerType layer) {
    super(logger);
    this.hash = hash;
    this.kind = AnnotationType.TLS;
    this.signatureInfo = signatureInfo;
    this.layer = layer;
  }

  private Boolean verifyHandshake(SSLSocket socket) {
    // a call to getSession tries to set up a session if there is no currently valid
    // session, and an implicit handshake is done.
    // If handshaking fails for any reason, the SSLSocket is closed, and no futher
    // communications can be done. 
    // from: https://docs.oracle.com/javase/7/docs/api/javax/net/ssl/SSLSocket.html
    socket.getSession();
    return !socket.isClosed();
  }

  public Annotation execute(PropertyBag ctx, byte[] data, String key) throws AnnotatorException {
    // get host name
    String host = "";
    try {
      host = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      this.logger.error("Error during TlsAnnotator execution: ",e);
    }

    // TLS check handshake
    Boolean isSatisfied = this.verifyHandshake(ctx.getProperty(AnnotationType.TLS.name(),
        SSLSocket.class));

    // create an annotation without signature
    final Annotation annotation = new Annotation(key, hash, host, layer, kind, null, isSatisfied, 
        ZonedDateTime.now());

    // sign annotation
    final String signature = super.signAnnotation(signatureInfo.getPrivateKey(),
        annotation);

    // append signature to annotation
    annotation.setSignature(signature);
    return annotation;
  }
}
