package com.alvarium.annotators;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;

import com.alvarium.contracts.Annotation;
import com.alvarium.contracts.AnnotationType;
import com.alvarium.hash.HashType;
import com.alvarium.sign.SignatureInfo;
import com.alvarium.utils.PropertyBag;

/**
 * A unit used to provide lineage from one version of data to another as a result of
 * change or transformation
 */
class SourceAnnotator extends AbstractAnnotator implements Annotator {
  private final HashType hash;
  private final AnnotationType kind;
  private final SignatureInfo signatureInfo;
  
  protected SourceAnnotator(HashType hash, SignatureInfo signatureInfo) {
    this.hash = hash;
    this.kind = AnnotationType.SOURCE;
    this.signatureInfo = signatureInfo;
  }  

  public Annotation execute(PropertyBag ctx, byte[] data) throws AnnotatorException {
    // hash incoming data
    final String key = super.deriveHash(this.hash, data);

    // get hostname if available
    final String host;
    try {
      host = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      throw new AnnotatorException("cannot get host name.", e);
    }

    // create an annotation without signature
    final Annotation annotation = new Annotation(key, this.hash, host, this.kind, null, true,
        Instant.now());
    
    final String signature = super.signAnnotation(signatureInfo.getPrivateKey(), annotation);
    annotation.setSignature(signature);
    return annotation;
  } 
}
