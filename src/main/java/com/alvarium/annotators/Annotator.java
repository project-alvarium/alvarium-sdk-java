package com.alvarium.annotators;

import com.alvarium.contracts.Annotation;
import com.alvarium.utils.PropertyBag;

/**
 * A unit responsible for annotating raw data and producing an Annotation object
 */
public interface Annotator {
  /**
   * creates an Annotation from the given raw data
   * @param ctx
   * @param data
   * @return Annotation object
   * @throws AnnotatorException
   */
  public Annotation execute(PropertyBag ctx, byte[] data) throws AnnotatorException;  
}
