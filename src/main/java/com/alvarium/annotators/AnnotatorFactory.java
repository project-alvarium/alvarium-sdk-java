package com.alvarium.annotators;

import com.alvarium.contracts.AnnotationType;
import com.alvarium.hash.HashType;
import com.alvarium.sign.SignatureInfo;

public class AnnotatorFactory {

  public Annotator getAnnotator(AnnotationType kind, HashType hash, SignatureInfo signature) 
      throws AnnotatorException {
    switch (kind) {
      case MOCK:
        return new MockAnnotator(hash, kind, signature);
      case TLS:
        return new TlsAnnotator(hash, signature);
      case PKI:
        return new PkiAnnotator(hash, signature);
      case TPM:
        return new TpmAnnotator(hash, signature);
      case SOURCE:
        return new SourceAnnotator(hash, signature);
      default:
        throw new AnnotatorException("Annotator type is not supported"); 
    }
  }  
}
