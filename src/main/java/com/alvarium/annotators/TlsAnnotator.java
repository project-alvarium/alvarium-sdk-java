package com.alvarium.annotators;

import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.net.ssl.SSLSocket;
import java.time.Instant;

import com.alvarium.contracts.Annotation;
import com.alvarium.contracts.AnnotationType;
import com.alvarium.hash.HashType;
import com.alvarium.sign.SignatureInfo;
import com.alvarium.utils.PropertyBag;

class TlsAnnotator extends AbstractAnnotator implements Annotator {
  private final HashType hash;
  private final AnnotationType kind;
  private final SignatureInfo signatureInfo;
  
  protected TlsAnnotator(HashType hash, SignatureInfo signatureInfo) {
    this.hash = hash;
    this.kind = AnnotationType.TLS;
    this.signatureInfo = signatureInfo;
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

  public Annotation execute(PropertyBag ctx, byte[] data) throws AnnotatorException {
    // hash incoming data
    final String key = super.deriveHash(hash, data);

    // get host name
    String host;
    try {
      host = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      throw new AnnotatorException("cannot get host name.", e);
    }

    // TLS check handshake
    Boolean isSatisfied = this.verifyHandshake(ctx.getProperty(AnnotationType.TLS.name(),
        SSLSocket.class));

    // create an annotation without signature
    final Annotation annotation = new Annotation(key, hash, host, kind, null, isSatisfied, 
        Instant.now());

    // sign annotation
    final String signature = super.signAnnotation(signatureInfo.getPrivateKey(),
        annotation);

    // append signature to annotation
    annotation.setSignature(signature);
    return annotation;
  }
}
