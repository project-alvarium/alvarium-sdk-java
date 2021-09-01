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
      default:
        throw new AnnotatorException("Annotator type is not supported"); 
    }
  }  
}
